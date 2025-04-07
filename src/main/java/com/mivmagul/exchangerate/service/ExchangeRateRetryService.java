package com.mivmagul.exchangerate.service;

import com.mivmagul.exchangerate.dto.CurrencyRate;
import com.mivmagul.exchangerate.provider.ExchangeRateProvider;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExchangeRateRetryService {

  private final List<ExchangeRateProvider> providers;

  public ExchangeRateRetryService(List<ExchangeRateProvider> providers) {
    this.providers = providers;
  }

  @Retryable(
      retryFor = {Exception.class},
      maxAttempts = 5,
      backoff = @Backoff(delay = 500, multiplier = 2))
  public Set<CurrencyRate> fetchRatesForBaseCurrency(String baseCurrency) {
    for (ExchangeRateProvider provider : providers) {
      try {
        log.info("Fetching rates from provider: {}", provider.getClass().getSimpleName());
        return provider.fetchRates(baseCurrency);
      } catch (Exception ex) {
        log.error(
            "Provider {} failed with: {}", provider.getClass().getSimpleName(), ex.getMessage());
      }
    }
    throw new RuntimeException("All providers failed to fetch exchange rates.");
  }
}
