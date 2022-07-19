/*
 * Copyright © 2022 JINSPIRED B.V.
 */

package io.substrates.spi.alpha;

import io.humainary.substrates.Substrates;
import io.humainary.substrates.spi.SubstratesProvider;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.State.BLOCKED;
import static java.lang.Thread.State.RUNNABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VariableTest {

  private static final SubstratesProvider PROVIDER =
    new Provider ();

  private static final Substrates.Name PATH =
    Names.of ( Variables.Variable.class );

  private static final Substrates.Environment EMPTY =
    ( name, defVal ) -> defVal;

  @Test
  void of_type () {

    final Map< ?, ? > defValue =
      new HashMap<> ( 5 );

    final Map< ?, ? > actual =
      new HashMap<> ( 5 );

    final var variable =
      Variables.of (
        PATH,
        Map.class,
        defValue
      );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      defValue,
      variable.of (
        EMPTY
      )
    );

    assertEquals (
      defValue,
      variable.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }


  @Test
  void of_object () {

    final var defValue =
      new Object ();

    final var actual =
      new Object ();

    final var variable =
      Variables.of (
        PATH,
        defValue
      );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      defValue,
      variable.of (
        EMPTY
      )
    );

  }


  @Test
  void of_int () {

    final Integer defVal = 1;
    final Integer actual = 2;

    final var variable =
      Variables.of (
        PATH,
        defVal
      );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual.toString ()
        )
      )
    );

    assertEquals (
      defVal,
      variable.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      variable.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }

  @Test
  void of_long () {

    final Long defVal = 1L;
    final Long actual = 2L;

    final var variable =
      Variables.of (
        PATH,
        defVal
      );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual.toString ()
        )
      )
    );

    assertEquals (
      defVal,
      variable.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      variable.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }

  @Test
  void of_double () {

    final Double defVal = 1.0D;
    final Double actual = 2.0D;

    final var variable =
      Variables.of (
        PATH,
        defVal
      );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual.toString ()
        )
      )
    );

    assertEquals (
      defVal,
      variable.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      variable.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }

  @Test
  void of_float () {

    final Float defVal = 1.0F;
    final Float actual = 2.0F;

    final var variable =
      Variables.of (
        PATH,
        defVal
      );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual.toString ()
        )
      )
    );

    assertEquals (
      defVal,
      variable.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      variable.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }

  @Test
  void of_string () {

    final var defVal = "1";
    final var actual = "2";

    final var variable =
      Variables.of (
        PATH,
        defVal
      );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      defVal,
      variable.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      variable.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }

  @Test
  void of_boolean () {

    final Boolean defVal = true;
    final Boolean actual = false;

    final var variable =
      Variables.of (
        PATH,
        defVal
      );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual.toString ()
        )
      )
    );

    assertEquals (
      defVal,
      variable.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      variable.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }

  @Test
  void of_name () {

    final var defVal =
      PROVIDER.name ( "1" );

    final var actual =
      PROVIDER.name ( "2" );

    final var variable =
      Variables.of (
        PATH,
        defVal
      );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual.toString ()
        )
      )
    );

    assertEquals (
      defVal,
      variable.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      variable.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );
  }

  @Test
  void of_enum () {

    final var defVal = BLOCKED;
    final var actual = RUNNABLE;

    final var variable =
      Variables.of (
        PATH,
        Thread.State.class,
        defVal
      );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual.toString ()
        )
      )
    );

    assertEquals (
      defVal,
      variable.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      variable.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );
  }

  @Test
  void of_alt_type () {

    final Long defVal = 1L;
    final Long actual = 2L;

    final var variable =
      Variables.of (
        PATH,
        Long.class,
        Number.class,
        Number::longValue,
        defVal
      );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      variable.of (
        PROVIDER.environment (
          PATH,
          actual.intValue ()
        )
      )
    );

    assertEquals (
      defVal,
      variable.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      variable.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }

}