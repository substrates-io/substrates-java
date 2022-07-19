/*
 * Copyright © 2022 JINSPIRED B.V.
 */

package io.substrates.spi.alpha;

import io.humainary.substrates.Substrates.Environment;
import io.humainary.substrates.Substrates.Lookup;
import io.humainary.substrates.Substrates.Name;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.humainary.substrates.Substrates.environment;
import static io.humainary.substrates.Substrates.lookup;
import static java.lang.System.getProperty;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * A utility class for constructing various different
 * implementations of the {@link Environment} interface.
 *
 * @author wlouth
 * @since 1.0
 */

final class Environments {

  private Environments () {}

  static final Environment SYSTEM =
    environment (
      lookup (
        path ->
          getProperty (
            path.toString ()
          )
      )
    ).memorize ();  // prevent repeated "expensive" system property lookups


  static Environment cache (
    final Environment environment
  ) {

    return
      new Cached (
        environment
      );

  }


  static Environment map (
    final Lookup< Object > function
  ) {

    return
      new Adaptor (
        function
      );

  }


  static Environment single (
    final Name name,
    final Object value
  ) {

    return
      new ObjectVal (
        name,
        value
      );

  }


  static Environment single (
    final Name name,
    final int value
  ) {

    return
      new IntVal (
        name,
        value
      );

  }


  static Environment single (
    final Name name,
    final long value
  ) {

    return
      new LongVal (
        name,
        value
      );

  }


  static Environment single (
    final Name name,
    final double value
  ) {

    return
      new DoubleVal (
        name,
        value
      );

  }


  static Environment single (
    final Name name,
    final float value
  ) {

    return
      new FloatVal (
        name,
        value
      );

  }


  static Environment single (
    final Name name,
    final boolean value
  ) {

    return
      new BooleanVal (
        name,
        value
      );

  }


  private abstract static class Abstract
    implements Environment {

    private static Environment map (
      final Function< ? super Name, Object > function
    ) {

      return
        Environments.map (
          Lookups.of (
            function
          )
        );

    }

    private static Environment chain (
      final Environment fallback,
      final Environment primary
    ) {

      return
        new Chained (
          fallback,
          primary
        );

    }

    @Override
    public Environment filter (
      final Predicate< ? super Name > filter
    ) {

      return
        new Filtered (
          this,
          filter
        );

    }

    @Override
    public Environment override (
      final Name name,
      final Object value
    ) {

      return
        ( lookup, defVal ) ->
          name == lookup
          ? value
          : get ( lookup, defVal );

    }


    @Override
    public Environment override (
      final Name name,
      final Supplier< Object > supplier
    ) {

      return
        override (
          map (
            path ->
              path == name
              ? ofNullable ( supplier.get () )
              : empty ()
          )
        );

    }

    @Override
    public Environment override (
      final Environment primary
    ) {

      return
        chain (
          this,
          primary
        );

    }

    @Override
    public Environment memorize () {

      return
        cache (
          this
        );

    }

    @Override
    public Environment remap (
      final Function< ? super Name, ? extends Name > mapper
    ) {

      return
        new Remap (
          this,
          mapper
        );
    }

  }


  private static final class Cached
    extends Abstract {

    private static final Object NONE =
      new Object ();

    private final Environment delegate;

    private final Map< Name, Object > cache =
      new ConcurrentHashMap<> ( 5 );

    Cached (
      final Environment delegate
    ) {

      this.delegate =
        delegate;

    }


    @Override
    public Optional< Object > get (
      final Name name
    ) {

      final var result =
        lookup (
          name
        );

      return
        result != NONE ?
        Optional.of ( result ) :
        empty ();

    }


    @Override
    public Object get (
      final Name name,
      final Object defValue
    ) {

      final var result =
        lookup (
          name
        );

      return
        result != NONE ?
        result :
        defValue;

    }

    private Object lookup (
      final Name name
    ) {

      final var result =
        cache.get (
          name
        );

      return
        result != null ?
        result :
        source ( name );

    }


    private Object source (
      final Name name
    ) {

      return
        cache
          .computeIfAbsent (
            name,
            key ->
              delegate
                .get ( key )
                .orElse ( NONE )
          );

    }

  }


  private static final class Adaptor
    extends Abstract {

    final Lookup< Object > lookup;

    Adaptor (
      final Lookup< Object > lookup
    ) {

      this.lookup =
        lookup;

    }

    @Override
    public Optional< Object > get (
      final Name name
    ) {

      return
        lookup
          .get (
            name
          );

    }

    @Override
    public Object get (
      final Name name,
      final Object defValue
    ) {

      return
        lookup.get (
          name
        ).orElse (
          defValue
        );

    }

  }


  private static final class Remap
    extends Abstract {

    private final Environment delegate;

    private final Function< ? super Name, ? extends Name > function;

    Remap (
      final Environment delegate,
      final Function< ? super Name, ? extends Name > mapper
    ) {

      this.delegate =
        delegate;

      function =
        mapper;

    }


    @Override
    public Object get (
      final Name name,
      final Object defVal
    ) {

      return
        delegate.get (
          function.apply (
            name
          ),
          defVal
        );

    }

  }


  private static final class Chained
    extends Abstract {

    final Environment fallback;
    final Environment primary;

    Chained (
      final Environment fallback,
      final Environment primary
    ) {

      this.fallback =
        fallback;

      this.primary =
        primary;

    }

    @Override
    public Object get (
      final Name name,
      final Object defValue
    ) {

      final var result =
        primary.get (
          name,
          null
        );

      return
        result != null
        ? result
        : fallback.get ( name, defValue );

    }

  }

  private static final class Filtered
    extends Abstract {

    final Environment               delegate;
    final Predicate< ? super Name > filter;

    Filtered (
      final Environment delegate,
      final Predicate< ? super Name > filter
    ) {

      this.delegate =
        delegate;

      this.filter =
        filter;

    }


    @Override
    public Object get (
      final Name name,
      final Object defValue
    ) {

      return
        filter.test ( name )
        ? delegate.get ( name, defValue )
        : null;

    }

  }

  private abstract static class AbstractVal< T >
    extends Abstract {

    final Name key;

    AbstractVal (
      final Name name
    ) {

      key =
        name;

    }


    @Override
    public Object get (
      final Name name,
      final Object defValue
    ) {

      return
        key == name
        ? value ()
        : defValue;

    }

    abstract T value ();

  }

  private static final class ObjectVal
    extends AbstractVal< Object > {

    private final Object val;

    ObjectVal (
      final Name name,
      final Object value
    ) {

      super (
        name
      );

      val =
        value;

    }


    @Override
    Object value () {

      return
        val;

    }

  }

  private static final class IntVal
    extends AbstractVal< Integer > {

    private final int value;

    IntVal (
      final Name name,
      final int value
    ) {

      super (
        name
      );

      this.value =
        value;

    }


    @Override
    public int getInteger (
      final Name name,
      final int defVal
    ) {

      return
        key == name
        ? value
        : defVal;

    }

    Integer value () {

      return
        value;

    }

  }

  private static final class LongVal
    extends AbstractVal< Long > {

    private final long value;

    LongVal (
      final Name name,
      final long value
    ) {

      super (
        name
      );

      this.value =
        value;

    }


    @Override
    public long getLong (
      final Name name,
      final long defVal
    ) {

      return
        key == name
        ? value
        : defVal;

    }

    Long value () {

      return
        value;

    }

  }

  private static final class FloatVal
    extends AbstractVal< Float > {

    private final float value;

    FloatVal (
      final Name name,
      final float value
    ) {

      super (
        name
      );

      this.value =
        value;

    }


    @Override
    public float getFloat (
      final Name name,
      final float defVal
    ) {

      return
        key == name
        ? value
        : defVal;

    }

    Float value () {

      return
        value;

    }

  }

  private static final class DoubleVal
    extends AbstractVal< Double > {

    private final double value;

    DoubleVal (
      final Name name,
      final double value
    ) {

      super (
        name
      );

      this.value =
        value;

    }


    @Override
    public double getDouble (
      final Name name,
      final double defVal
    ) {

      return
        key == name
        ? value
        : defVal;

    }

    Double value () {

      return
        value;

    }

  }

  private static final class BooleanVal
    extends AbstractVal< Boolean > {

    private final boolean value;

    BooleanVal (
      final Name name,
      final boolean value
    ) {

      super (
        name
      );

      this.value =
        value;

    }


    @Override
    public boolean getBoolean (
      final Name name,
      final boolean defVal
    ) {

      return
        key == name
        ? value
        : defVal;

    }

    Boolean value () {

      return
        value;

    }

  }


}
