/*
 * Copyright © 2022 JINSPIRED B.V.
 */

package io.substrates.spi.alpha;

import io.humainary.spi.Providers.Factory;
import io.humainary.substrates.spi.SubstratesProvider;

/**
 * The SPI provider factory implementation of {@link SubstratesProvider}.
 *
 * @author wlouth
 * @since 1.0
 */

public final class ProviderFactory
  implements Factory< SubstratesProvider > {

  @Override
  public SubstratesProvider create () {

    return
      new Provider ();

  }

}
