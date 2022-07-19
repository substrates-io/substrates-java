package io.substrates.spi.alpha;

import io.humainary.substrates.Substrates;
import io.humainary.substrates.Substrates.Environment;
import io.humainary.substrates.Substrates.Name;
import io.humainary.substrates.Substrates.Type;

final class References {

  private References () {}

  static Substrates.Reference of (
    final Type type,
    final Name name,
    final Environment environment
  ) {

    return
      new Reference (
        type,
        name,
        environment
      );

  }

  private record Reference(
    Type type,
    Name name,
    Environment environment
  ) implements Substrates.Reference {}

}
