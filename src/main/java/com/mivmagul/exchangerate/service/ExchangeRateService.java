package com.mivmagul.exchangerate.service;

import com.mivmagul.exchangerate.dto.CurrencyRate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
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
    Set<CurrencyRate> baseRates = retryService.fetchRatesForBaseCurrency(baseCurrency);

    BigDecimal rateTo = findRate(baseRates, currencyTo);
    BigDecimal rateFrom = findRate(baseRates, currencyFrom);

    if (baseCurrency.equalsIgnoreCase(currencyFrom)) {
      return rateTo;
    } else if (baseCurrency.equalsIgnoreCase(currencyTo)) {
      return BigDecimal.ONE.divide(rateFrom, precisionScale, RoundingMode.HALF_UP);
    } else {
      return rateTo.divide(rateFrom, precisionScale, RoundingMode.HALF_UP);
    }
  }

  private BigDecimal findRate(Set<CurrencyRate> baseRates, String currency) {
    for (CurrencyRate rate : baseRates) {
      if (rate.getCurrencyCode().equalsIgnoreCase(currency)) {
        return rate.getRate();
      }
    }
    throw new RuntimeException("Rate not found for currency: " + currency);
  }

  public Set<CurrencyRate> getAllExchangeRates(String baseCurrency) {
    return retryService.fetchRatesForBaseCurrency(baseCurrency);
  }

  public BigDecimal convertValue(String currencyFrom, String currencyTo, BigDecimal amount) {
    BigDecimal exchangeRate = getExchangeRate(currencyFrom, currencyTo);
    return amount.multiply(exchangeRate);
  }

  public Set<CurrencyRate> convertToMultipleCurrencies(
      String currencyFrom, BigDecimal amount, List<String> currencies) {
    return currencies.stream()
        .map(currency -> new CurrencyRate(currency, convertValue(currencyFrom, currency, amount)))
        .collect(Collectors.toSet());
  }
}
