/*
 * Copyright © 2022 JINSPIRED B.V.
 */

package io.substrates.spi.alpha;

import io.humainary.substrates.Substrates;
import io.humainary.substrates.Substrates.Environment;
import io.humainary.substrates.Substrates.Hub;
import io.humainary.substrates.Substrates.Lookup;
import io.humainary.substrates.Substrates.Name;
import io.humainary.substrates.spi.SubstratesProvider;
import io.substrates.spi.alpha.Variables.Variable;

import java.lang.reflect.Member;
import java.util.function.Function;

import static io.substrates.spi.alpha.Environments.*;
import static java.util.Objects.requireNonNull;

/**
 * The SPI implementation of {@link SubstratesProvider}.
 *
 * @author wlouth
 * @since 1.0
 */

final class Provider
  implements SubstratesProvider {

  @Override
  public Names.Name name (
    final String path
  ) {

    return
      Names.of (
        path
      );

  }


  @Override
  public Names.Name name (
    final Class< ? > cls
  ) {

    return
      Names.of (
        cls
      );

  }


  @Override
  public Name name (
    final Member member
  ) {

    return
      Names.of (
        requireNonNull (
          member
        )
      );

  }


  @Override
  public Environment environment () {

    return
      SYSTEM;

  }


  @Override
  public Environment environment (
    final Lookup< Object > lookup
  ) {

    return
      cache (
        map (
          lookup
        )
      );

  }


  @Override
  public < T > Environment environment (
    final Name path,
    final T value
  ) {

    return
      single (
        path,
        value
      );

  }

  @Override
  public Environment environment (
    final Name path,
    final int value
  ) {

    return
      single (
        path,
        value
      );
  }

  @Override
  public Environment environment (
    final Name path,
    final long value
  ) {

    return
      single (
        path,
        value
      );

  }

  @Override
  public Environment environment (
    final Name path,
    final float value
  ) {

    return
      single (
        path,
        value
      );

  }

  @Override
  public Environment environment (
    final Name path,
    final double value
  ) {

    return
      single (
        path,
        value
      );

  }

  @Override
  public Environment environment (
    final Name path,
    final boolean value
  ) {

    return
      single (
        path,
        value
      );

  }


  @Override
  public Variable< Object > variable (
    final Name name,
    final Object defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public < T > Variable< T > variable (
    final Name name,
    final Class< ? extends T > type,
    final T defValue
  ) {

    return
      Variables.of (
        name,
        type,
        defValue
      );

  }

  @Override
  public < T, A > Variable< T > variable (
    final Name name,
    final Class< ? extends T > type,
    final Class< ? extends A > alt,
    final Function< ? super A, ? extends T > mapper,
    final T defValue
  ) {

    return
      Variables.of (
        name,
        type,
        alt,
        mapper,
        defValue
      );

  }

  @SuppressWarnings ( "BoundedWildcard" )
  @Override
  public < T extends Enum< T > > Variable< T > variable (
    final Name name,
    final Class< T > type,
    final T defValue
  ) {

    return
      Variables.of (
        name,
        type,
        defValue
      );

  }

  @Override
  public Variable< Boolean > variable (
    final Name name,
    final Boolean defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public Variable< Integer > variable (
    final Name name,
    final Integer defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public Variable< Long > variable (
    final Name name,
    final Long defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public Variable< Double > variable (
    final Name name,
    final Double defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public Variable< Float > variable (
    final Name name,
    final Float defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public Variable< String > variable (
    final Name name,
    final String defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public Variable< CharSequence > variable (
    final Name name,
    final CharSequence defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public Variable< Name > variable (
    final Name name,
    final Name defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }


  @Override
  public < E > Hub< E > hub () {

    return
      Hubs.of ();

  }

  @Override
  public < E > Hub< E > hub (
    final Environment environment
  ) {

    return
      Hubs.of (
        environment
      );

  }

  @Override
  public < T > Lookup< T > lookup (
    final Function< ? super Name, ? extends T > fn
  ) {

    return
      Lookups.of (
        fn
      );

  }

  @Override
  public Substrates.Reference reference (
    final Substrates.Type type,
    final Name name,
    final Environment environment
  ) {

    return
      References.of (
        type,
        name,
        environment
      );

  }

  @Override
  public Substrates.Type type (
    final Class< ? extends Substrates.Referent > type
  ) {

    return
      Types.of (
        Names.of (
          type
        )
      );

  }

}
