/*
 * Copyright © 2022 JINSPIRED B.V.
 */

package io.substrates.spi.alpha;

import io.humainary.substrates.Substrates;
import io.humainary.substrates.Substrates.Environment;
import io.humainary.substrates.Substrates.Name;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author wlouth
 * @since 1.0
 */

@SuppressWarnings ( "unchecked" )
final class Variables {

  @FunctionalInterface
  private interface Decoder< T > {

    T decode ( String string );

  }

  private static < T > T decode (
    final Object value,
    final Decoder< ? extends T > decoder,
    final T defVal
  ) {

    if ( value instanceof String string ) {

      try {

        return
          decoder.decode (
            string
          );

      } catch (
        final Exception e
      ) {
        /* fallback to defValue */
      }
    }

    return
      defVal;

  }

  private static < T extends Number > T convert (
    final Object value,
    final T defVal,
    final Decoder< ? extends T > decoder,
    final UnaryOperator< T > converter,
    final Class< T > type
  ) {

    if ( type.isInstance ( value ) ) {

      return
        converter.apply (
          (T) value
        );

    } else {

      return
        decode (
          value,
          decoder,
          defVal
        );

    }

  }

  private static < T, A > T toAltType (
    final Class< ? extends T > type,
    final Class< ? extends A > alt,
    final Function< ? super A, ? extends T > mapper,
    final Object value,
    final T defVal
  ) {

    if ( type.isInstance ( value ) ) {

      return
        (T) value;

    } else if ( alt.isInstance ( value ) ) {

      return
        mapper.apply (
          (A) value
        );

    } else {

      return
        defVal;

    }

  }

  private static < T extends Enum< T > > T toEnum (
    final Class< T > type,
    final Object value,
    final T defVal
  ) {

    if ( type.isInstance ( value ) ) {

      return
        (T) value;

    } else {

      return
        decode (
          value,
          string ->
            Enum.valueOf (
              type,
              string
            ),
          defVal
        );

    }

  }

  private static Boolean toBoolean (
    final Object value,
    final Boolean defVal
  ) {

    if ( value instanceof Boolean result ) {

      return
        result;

    } else {

      return
        decode (
          value,
          Boolean::parseBoolean,
          defVal
        );

    }

  }

  private static Integer toInteger (
    final Object value,
    final Integer defVal
  ) {

    if ( value instanceof Integer result ) {

      return
        result;

    } else {

      return
        convert (
          value,
          defVal,
          Integer::parseInt,
          Number::intValue,
          Integer.class
        );

    }

  }

  private static Long toLong (
    final Object value,
    final Long defVal
  ) {

    if ( value instanceof Long result ) {

      return
        result;

    } else {

      return
        convert (
          value,
          defVal,
          Long::parseLong,
          Number::longValue,
          Long.class
        );

    }

  }

  private static Double toDouble (
    final Object value,
    final Double defVal
  ) {

    if ( value instanceof Double result ) {

      return
        result;

    } else {

      return
        convert (
          value,
          defVal,
          Double::parseDouble,
          Number::doubleValue,
          Double.class
        );
    }

  }

  private static Float toFloat (
    final Object value,
    final Float defVal
  ) {

    if ( value instanceof Float result ) {

      return
        result;

    } else {

      return
        convert (
          value,
          defVal,
          Float::parseFloat,
          Number::floatValue,
          Float.class
        );
    }

  }

  private static Object toObject (
    final Object value,
    final Object defVal
  ) {

    return
      value != null
      ? value
      : defVal;

  }

  private static String toString (
    final Object value,
    final String defVal
  ) {

    return
      value instanceof String string
      ? string
      : defVal;

  }

  private static CharSequence toCharSequence (
    final Object value,
    final CharSequence defVal
  ) {

    return
      value instanceof CharSequence sequence
      ? sequence
      : defVal;

  }

  private static Name toName (
    final Object value,
    final Name defVal
  ) {

    if ( value instanceof Name name ) {

      return
        name;

    } else {

      return
        decode (
          value,
          Names::of,
          defVal
        );
    }

  }

  private Variables () {}

  /**
   * Creates a {@link Substrates.Variable} of type {@code Object}.
   *
   * @param name   the name of the configuration item
   * @param defVal the value to be used if the variable is not present in an environment passed
   * @return A {@code Variable} of type {@code Object}
   */

  static Variable< Object > of (
    final Name name,
    final Object defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toObject
      );

  }

  /**
   * Creates a {@link Substrates.Variable} of type {@code T}.
   *
   * @see Environment#getObject(Name, Class, Object)
   */

  static < T > Variable< T > of (
    final Name name,
    final Class< ? extends T > type,
    final T defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        ( val, def ) ->
          type.isInstance ( val )
          ? (T) val
          : def
      );

  }

  /**
   * Creates a {@link Substrates.Variable} of type {@code T}.
   *
   * @see Environment#getObject(Name, Class, Class, Function)
   */

  static < T, A > Variable< T > of (
    final Name name,
    final Class< ? extends T > type,
    final Class< ? extends A > alt,
    final Function< ? super A, ? extends T > mapper,
    final T defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        ( val, def ) ->
          toAltType (
            type,
            alt,
            mapper,
            val,
            def
          )
      );

  }

  /**
   * Creates a {@link Substrates.Variable} of type {@code Enum}.
   *
   * @see Environment#getEnum(Name, Class, Enum)
   */

  static < T extends Enum< T > > Variable< T > of (
    final Name name,
    final Class< T > type,
    final T defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        ( val, def ) ->
          toEnum (
            type,
            val,
            def
          )
      );

  }

  /**
   * Creates a {@link Substrates.Variable} of type {@code Boolean}.
   *
   * @see Environment#getBoolean(Name, boolean)
   */

  static Variable< Boolean > of (
    final Name name,
    final Boolean defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toBoolean
      );

  }

  /**
   * Creates a {@link Substrates.Variable} of type {@code Integer}.
   *
   * @see Environment#getInteger(Name, int)
   */

  static Variable< Integer > of (
    final Name name,
    final Integer defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toInteger
      );

  }

  /**
   * Creates a {@link Substrates.Variable} of type {@code Long}.
   *
   * @see Environment#getLong(Name, long)
   */

  static Variable< Long > of (
    final Name name,
    final Long defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toLong
      );

  }

  /**
   * Creates a {@link Substrates.Variable} of type {@code Double}.
   *
   * @see Environment#getDouble(Name, double)
   */

  static Variable< Double > of (
    final Name name,
    final Double defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toDouble
      );

  }

  /**
   * Creates a {@link Substrates.Variable} of type {@code String}.
   *
   * @see Environment#getString(Name, String)
   */

  static Variable< String > of (
    final Name name,
    final String defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toString
      );


  }

  /**
   * Creates a {@link Substrates.Variable} of type {@code Float}.
   *
   * @see Environment#getFloat(Name, float)
   */

  static Variable< Float > of (
    final Name name,
    final Float defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toFloat
      );

  }

  /**
   * Creates a {@link Substrates.Variable} of type {@code Name}.
   *
   * @see Environment#getName(Name, Name)
   */

  static Variable< Name > of (
    final Name name,
    final Name defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toName
      );

  }

  /**
   * Creates a {@link Substrates.Variable} of type {@code CharSequence}.
   *
   * @see Environment#getCharSequence(Name, CharSequence)
   */

  static Variable< CharSequence > of (
    final Name name,
    final CharSequence defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toCharSequence
      );

  }

  static final class Variable< T >
    implements Substrates.Variable< T > {

    private final Name                                         name;
    private final T                                            defVal;
    private final BiFunction< Object, ? super T, ? extends T > mapper;

    Variable (
      final Name name,
      final T defVal,
      final BiFunction< Object, ? super T, ? extends T > mapper
    ) {

      this.name =
        name;

      this.defVal =
        defVal;

      this.mapper =
        mapper;

    }

    public T of (
      final Environment environment
    ) {

      final var fallback =
        defVal;

      final var value =
        environment.get (
          name,
          fallback
        );

      return
        value == fallback
        ? fallback
        : mapper.apply ( value, fallback );

    }

  }

}
