/*
 * Copyright © 2022 JINSPIRED B.V.
 */

package io.substrates.spi.alpha;

import io.humainary.substrates.Substrates;

import java.lang.reflect.Member;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;

/**
 * @author wlouth
 * @since 1.0
 */

final class Names {

  private static final char                              DOT     = '.';
  private static final int                               INDEX   = DOT;
  private static final Pattern                           PATTERN = compile ( "\\" + DOT );
  private static final ConcurrentHashMap< String, Name > MAP     =
    new ConcurrentHashMap<> ( 1009 );

  private static IllegalArgumentException illegalArgument (
    final String path
  ) {

    return
      new IllegalArgumentException (
        "Invalid Path Specification: " + path
      );

  }

  private static Name lookup (
    final String path
  ) {

    final var name =
      MAP.get (
        path
      );

    return
      name != null ?
      name :
      parse (
        path
      );

  }

  private static Name parse (
    final String path
  ) {

    final var name =
      checkName (
        parseOrNull (
          path
        ),
        path
      );

    MAP.put (
      path,
      name
    );

    return
      name;

  }

  private static Name parseOrNull (
    final CharSequence path
  ) {

    final var closure =
      new Closure ();

    splitAsStream (
      path
    ).forEachOrdered (

      part ->
        addNameTo (
          part,
          closure
        )

    );

    return
      closure.value;

  }

  private static void addNameTo (
    final String part,
    final Closure closure
  ) {

    final var prefix =
      closure.value;

    closure.value =
      prefix == null ?
      root ( part ) :
      prefix.node ( part );

  }


  private Names () {}

  static Name of (
    final Class< ? > cls
  ) {

    return
      lookup (
        cls.getName ()
      );

  }

  static Name of (
    final Member member
  ) {

    return
      lookup (
        member.getDeclaringClass ().getName ()
      ).node (
        member.getName ()
      );

  }

  private static Stream< String > splitAsStream (
    final CharSequence name
  ) {

    return
      PATTERN.splitAsStream (
        name
      ).filter (
        IS_NOT_EMPTY
      );

  }

  private static final Predicate< String > IS_NOT_EMPTY = Predicate.not ( String::isEmpty );

  private static < T > T getOrAdd (
    final String path,
    final ConcurrentHashMap< String, T > names,
    final Function< ? super String, ? extends T > mapper
  ) {

    final var name =
      names.get (
        path
      );

    return
      name != null ?
      name :
      names.computeIfAbsent (
        path,
        mapper
      );

  }

  static Name of (
    final String path
  ) {

    checkPath (
      path
    );

    return
      isCompositePath (
        path
      ) ?
      lookup (
        path
      ) :
      root ( path );

  }

  private static Name root (
    final String path
  ) {

    return
      getOrAdd (
        path,
        MAP,
        Name::new
      );

  }

  private static void checkPath (
    final String path
  ) {

    if ( path.isEmpty () ) {

      throw
        illegalArgument (
          path
        );

    }

  }

  private static boolean isCompositePath (
    final String path
  ) {

    return
      path.indexOf (
        INDEX
      ) != -1;

  }

  private static < T extends Substrates.Name > T checkName (
    final T name,
    final String path
  ) {

    if ( name == null ) {
      throw
        illegalArgument (
          path
        );
    }

    return name;

  }

  /**
   * The SPI implementation of {@link Substrates.Name}.
   *
   * @author wlouth
   * @since 1.0
   */

  @SuppressWarnings ( "unchecked" )
  static final class Name
    implements Substrates.Name {

    @SuppressWarnings ( "rawtypes" )
    private static final AtomicReferenceFieldUpdater< Name, ConcurrentHashMap > UPDATER =
      AtomicReferenceFieldUpdater.newUpdater (
        Name.class,
        ConcurrentHashMap.class,
        "cache"
      );

    private final String                            val;
    private final Name                              prefix;
    volatile      ConcurrentHashMap< String, Name > cache;
    private       String                            fqn;

    private static ConcurrentHashMap< String, Name > createCache () {

      return new ConcurrentHashMap<> ( 5 );

    }

    private static < T > T foldTo (
      final Name name,
      final Function< ? super Name, ? extends T > initial,
      final BiFunction< ? super T, ? super Name, T > accumulator
    ) {

      final var prefix =
        name.prefix;

      return
        prefix != null ?
        accumulator.apply (
          foldTo (
            prefix,
            initial,
            accumulator
          ),
          name
        ) :
        initial.apply (
          name
        );

    }

    private static < T > T foldFrom (
      final Name name,
      final BiFunction< ? super T, ? super Name, T > accumulator,
      final T value
    ) {

      return
        name != null
        ? name.foldFrom ( accumulator, value )
        : value;

    }


    Name (
      final String value
    ) {

      prefix =
        null;

      val =
        value;

    }

    private Name (
      final Name prefix,
      final String value
    ) {

      this.prefix =
        prefix;

      val =
        value;

    }

    Name left () {

      return
        prefix;

    }

    private String path () {

      return
        foldTo (
          this,
          seed ->
            new StringBuilder (
              seed.val
            ),
          ( builder, node ) ->
            builder.append (
              DOT
            ).append (
              node.val
            )
        ).toString ();

    }

    @Override
    public String toString () {

      return
        toPath ();

    }

    Name node (
      final String single
    ) {

      return
        getOrAdd (
          single,
          cache (),
          value ->
            new Name (
              this,
              value
            )
        );

    }

    Name node (
      final Enum< ? > value
    ) {

      return
        node (
          value.name ()
        );

    }

    private Name getOrParse (
      final String string
    ) {

      final var nodes =
        cache ();

      final var node =
        nodes.get (
          string
        );

      return
        node != null
        ? node
        : parseAndMaybeCacheNode ( string, nodes );

    }

    private Name parseAndMaybeCacheNode (
      final String string,
      final Map< ? super String, ? super Name > nodes
    ) {

      final var node =
        checkName (
          parseNodeOrNull (
            string
          ),
          string
        );

      if ( isNotChild ( node ) ) {

        nodes.put (
          string,
          node
        );

      }

      return
        node;

    }

    private boolean isNotChild (
      final Name child
    ) {

      return
        child.prefix != this;

    }

    private Name parseNodeOrNull (
      final CharSequence sequence
    ) {

      return
        splitAsStream (
          sequence
        ).reduce (
          null,
          ( name, single ) ->
            name == null
            ? node ( single )
            : name.node ( single ),
          ( n1, n2 ) -> n2
        );

    }

    private ConcurrentHashMap< String, Name > cache () {

      final var map =
        cache;

      return
        map != null ?
        map :
        updateAndGetCache ();

    }

    private ConcurrentHashMap< String, Name > updateAndGetCache () {

      return
        UPDATER
          .updateAndGet (
            this,
            prev ->
              prev == null
              ? createCache ()
              : prev
          );

    }

    @Override
    public String value () {

      return
        val;

    }

    @Override
    public Optional< Substrates.Name > enclosure () {

      return
        ofNullable (
          prefix
        );

    }

    @Override
    public Name name (
      final String path
    ) {

      requireNonNull (
        path
      );

      checkPath (
        path
      );

      return
        isCompositePath ( path )
        ? getOrParse ( path )
        : node ( path );

    }

    @Override
    public Name name (
      final Enum< ? > value
    ) {

      return
        node (
          value
        );

    }

    @Override
    public Name name (
      final Substrates.Name path
    ) {

      //noinspection CastToConcreteClass
      return
        name (
          (Name) path
        );

    }

    @Override
    public < T > T foldTo (
      final Function< ? super Substrates.Name, ? extends T > initial,
      final BiFunction< ? super T, ? super Substrates.Name, T > accumulator
    ) {

      return
        foldTo (
          this,
          initial,
          accumulator
        );

    }

    @Override
    public < T > T foldFrom (
      final Function< ? super Substrates.Name, ? extends T > initial,
      final BiFunction< ? super T, ? super Substrates.Name, T > accumulator
    ) {

      final var name =
        prefix;

      final var seed =
        initial.apply (
          this
        );

      return
        name != null
        ? name.foldFrom ( accumulator, seed )
        : seed;

    }

    @Override
    public java.util.Iterator< Substrates.Name > iterator () {

      return
        new Iterator (
          this
        );

    }

    Names.Name name (
      final Names.Name path
    ) {

      return
        path
          .foldTo (
            node (
              path.val
            ),
            ( t, p ) ->
              t.node (
                p.val
              )
          );

    }

    private < T > T foldFrom (
      final BiFunction< ? super T, ? super Name, T > accumulator,
      final T seed
    ) {

      return
        foldFrom (
          prefix,
          accumulator,
          accumulator.apply (
            seed,
            this
          )
        );

    }

    private < T > T foldTo (
      final T initial,
      final BiFunction< T, Name, T > accumulator
    ) {

      return
        prefix != null
        ? accumulator
          .apply (
            prefix.foldTo (
              initial,
              accumulator
            ),
            this
          )
        : initial;

    }

    @Override
    public void forEach (
      final Consumer< ? super Substrates.Name > action
    ) {

      var name = this;

      do {

        action.accept (
          name
        );

      } while (
        ( name = name.prefix ) != null
      );

    }

    @Override
    public String toPath () {

      final var result =
        fqn;

      return
        result != null
        ? result
        : ( fqn = path () );

    }

    private static final class Iterator
      implements java.util.Iterator< Substrates.Name > {

      Name name;

      Iterator (
        final Name name
      ) {

        this.name =
          name;

      }

      @Override
      public boolean hasNext () {

        return
          name != null;

      }

      @Override
      public Name next () {

        final var result =
          name;

        if ( result != null ) {

          name =
            result.left ();

          return
            result;

        }

        throw
          new NoSuchElementException ();

      }

    }


  }

  /*
   * A struct like class for reduce/collect like operations
   */

  private static final class Closure {

    Name value;

    Closure () {}

  }

}
