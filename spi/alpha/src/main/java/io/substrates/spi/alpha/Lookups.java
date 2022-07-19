/*
 * Copyright © 2022 JINSPIRED B.V.
 */

package io.substrates.spi.alpha;

import io.humainary.substrates.Substrates.Lookup;
import io.humainary.substrates.Substrates.Name;

import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

final class Lookups {

  private Lookups () {}

  static < T > Lookup< T > of (
    final Function< ? super Name, ? extends T > fn
  ) {

    requireNonNull (
      fn
    );

    return
      new FnLookup<> (
        fn
      );

  }

  @SuppressWarnings ( "ClassCanBeRecord" )
  private static final class FnLookup< T >
    implements Lookup< T > {

    private final Function< ? super Name, ? extends T > fn;

    private FnLookup (
      final Function< ? super Name, ? extends T > fn
    ) {

      this.fn =
        fn;

    }

    @Override
    public Optional< T > get (
      final Name name
    ) {

      try {

        return
          ofNullable (
            fn.apply (
              name
            )
          );

      } catch (
        final Throwable error
      ) {

        return
          empty ();

      }

    }

    @Override
    public T get (
      final Name name,
      final T defVal
    ) {

      try {

        final var result =
          fn.apply (
            name
          );

        return
          result != null
          ? result
          : defVal;

      } catch (
        final Throwable error
      ) {

        return
          defVal;

      }

    }

  }

}
