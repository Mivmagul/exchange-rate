package com.mivmagul.exchangerate.provider;

import com.mivmagul.exchangerate.data.ExchangeRateProviderType;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateProviderFactory {
  private final FixerExchangeRateProvider fixerProvider;
  private final ExchangeRateHostProvider hostProvider;

  public ExchangeRateProviderFactory(
      FixerExchangeRateProvider fixerProvider, ExchangeRateHostProvider hostProvider) {
    this.fixerProvider = fixerProvider;
    this.hostProvider = hostProvider;
  }

  public ExchangeRateProvider getProvider(ExchangeRateProviderType providerType) {
    return switch (providerType) {
      case FIXER_IO -> fixerProvider;
      case EXCHANGE_RATE_HOST -> hostProvider;
    };
  }
}
