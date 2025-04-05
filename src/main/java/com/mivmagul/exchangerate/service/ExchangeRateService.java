package com.mivmagul.exchangerate.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService {
  private final ExchangeRateRetryService retryService;
  private final String baseCurrency;
  private final int precisionScale;

  public ExchangeRateService(
      ExchangeRateRetryService retryService,
      @Value("${exchange-rate.base-currency}") String baseCurrency,
      @Value("${exchange-rate.precision-scale}") int precisionScale) {
    this.retryService = retryService;
    this.baseCurrency = baseCurrency;
    this.precisionScale = precisionScale;
  }

  public BigDecimal getExchangeRate(String currencyFrom, String currencyTo) {
    Map<String, BigDecimal> baseRates = retryService.fetchRatesForBaseCurrency(baseCurrency);
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
    return retryService.fetchRatesForBaseCurrency(baseCurrency);
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
