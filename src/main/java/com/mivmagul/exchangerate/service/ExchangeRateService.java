package com.mivmagul.exchangerate.service;

import com.mivmagul.exchangerate.data.ExchangeRateProviderType;
import com.mivmagul.exchangerate.provider.ExchangeRateProvider;
import com.mivmagul.exchangerate.provider.ExchangeRateProviderFactory;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService {

  private final ExchangeRateProvider defaultProvider;

  public ExchangeRateService(
      ExchangeRateProviderFactory providerFactory,
      @Value("${exchange-rate.default-provider}") ExchangeRateProviderType defaultProviderType) {
    this.defaultProvider = providerFactory.getProvider(defaultProviderType);
  }

  public Number getExchangeRate(String from, String to) {
    Map<String, Object> response = defaultProvider.fetchRates(from);
    Map<String, Number> rates = (Map<String, Number>) response.get("rates");
    return rates.get(to);
  }

  public Map<String, Number> getAllExchangeRates(String from) {
    Map<String, Object> response = defaultProvider.fetchRates(from);
    return (Map<String, Number>) response.get("rates");
  }

  public Number convertValue(String from, String to, BigDecimal amount) {
    Number rate = getExchangeRate(from, to);
    return amount.multiply(BigDecimal.valueOf(rate.doubleValue()));
  }

  public Map<String, BigDecimal> convertToMultipleCurrencies(
      String from, BigDecimal amount, List<String> currencies) {
    Map<String, Number> rates = getAllExchangeRates(from);
    Map<String, BigDecimal> conversions = new HashMap<>();
    for (String currency : currencies) {
      conversions.put(
          currency, BigDecimal.valueOf(rates.get(currency).doubleValue()).multiply(amount));
    }
    return conversions;
  }
}
