package com.mivmagul.exchangerate.service;

import com.mivmagul.exchangerate.provider.ExchangeRateProvider;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateRetryService {
  private static final Logger logger = LoggerFactory.getLogger(ExchangeRateRetryService.class);

  private final List<ExchangeRateProvider> providers;

  public ExchangeRateRetryService(List<ExchangeRateProvider> providers) {
    this.providers = providers;
  }

  @Retryable(
      retryFor = {Exception.class},
      maxAttempts = 5,
      backoff = @Backoff(delay = 500, multiplier = 2))
  public Map<String, BigDecimal> fetchRatesForBaseCurrency(String baseCurrency) {
    for (ExchangeRateProvider provider : providers) {
      try {
        Map<String, Object> response = provider.fetchRates(baseCurrency);
        return ((Map<String, Number>) response.get("rates"))
            .entrySet().stream()
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> BigDecimal.valueOf(entry.getValue().doubleValue())));
      } catch (Exception exception) {
        logger.error(
            "Provider {} failed with: {}",
            provider.getClass().getSimpleName(),
            exception.getMessage());
      }
    }
    throw new RuntimeException("All providers failed to fetch exchange rates.");
  }
}
