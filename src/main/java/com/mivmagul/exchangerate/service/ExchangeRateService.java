package com.mivmagul.exchangerate.service;

import com.mivmagul.exchangerate.provider.ExchangeRateProvider;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService {
  private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);

  private final String baseCurrency;
  private final int precisionScale;
  private final List<ExchangeRateProvider> providers;

  public ExchangeRateService(
      List<ExchangeRateProvider> providers,
      @Value("${exchange-rate.base-currency}") String baseCurrency,
      @Value("${exchange-rate.precision-scale}") int precisionScale) {
    this.providers = providers;
    this.baseCurrency = baseCurrency;
    this.precisionScale = precisionScale;
  }

  public Map<String, BigDecimal> fetchRatesForBaseCurrency(String baseCurrency) {
    if (baseCurrency == null) {
      throw new IllegalArgumentException("Base currency cannot be null");
    }

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

  public BigDecimal getExchangeRate(String currencyFrom, String currencyTo) {
    Map<String, BigDecimal> baseRates = fetchRatesForBaseCurrency(baseCurrency);

    if (baseCurrency.equalsIgnoreCase(currencyFrom)) {
      return baseRates.get(currencyTo);
    } else if (baseCurrency.equalsIgnoreCase(currencyTo)) {
      return BigDecimal.ONE.divide(
          baseRates.get(currencyFrom), precisionScale, RoundingMode.HALF_UP);
    } else {
      BigDecimal rateTo = baseRates.get(currencyTo);
      BigDecimal rateFrom = baseRates.get(currencyFrom);
      return rateTo.divide(rateFrom, precisionScale, RoundingMode.HALF_UP);
    }
  }

  public Map<String, BigDecimal> getAllExchangeRates(String baseCurrency) {
    return fetchRatesForBaseCurrency(baseCurrency);
  }

  public BigDecimal convertValue(String currencyFrom, String currencyTo, BigDecimal amount) {
    BigDecimal exchangeRate = getExchangeRate(currencyFrom, currencyTo);
    return amount.multiply(exchangeRate);
  }

  public Map<String, BigDecimal> convertToMultipleCurrencies(
      String currencyFrom, BigDecimal amount, List<String> currencies) {
    return currencies.stream()
        .collect(
            Collectors.toMap(
                currency -> currency, currency -> convertValue(currencyFrom, currency, amount)));
  }
}
