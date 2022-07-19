package io.substrates.spi.alpha;

import io.humainary.substrates.Substrates;
import io.substrates.spi.alpha.Names.Name;

import java.util.concurrent.ConcurrentHashMap;

final class Types {

  private Types () {}

  private record Type(
    Name name
  ) implements Substrates.Type {}

  private static final ConcurrentHashMap< Name, Type > MAP =
    new ConcurrentHashMap<> ( 109 );

  static Substrates.Type of (
    final Name name
  ) {

    return
      lookup (
        name
      );

  }

  private static Type lookup (
    final Name name
  ) {

    final var type =
      MAP.get (
        name
      );

    return
      type != null ?
      type :
      MAP.computeIfAbsent (
        name,
        Type::new
      );

  }

}
