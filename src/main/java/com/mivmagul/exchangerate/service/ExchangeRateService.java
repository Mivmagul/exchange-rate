package com.mivmagul.exchangerate.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService {

  private final ExchangeRateCacheService exchangeRateCacheService;

  public ExchangeRateService(ExchangeRateCacheService exchangeRateCacheService) {
    this.exchangeRateCacheService = exchangeRateCacheService;
  }

  public Number getExchangeRate(String from, String to) {
    Map<String, Object> response = exchangeRateCacheService.fetchRates(from);
    Map<String, Number> rates = (Map<String, Number>) response.get("rates");
    return rates.get(to);
  }

  public Map<String, Number> getAllExchangeRates(String from) {
    Map<String, Object> response = exchangeRateCacheService.fetchRates(from);
    return (Map<String, Number>) response.get("rates");
  }

  public Number convertValue(String from, String to, Double amount) {
    Number rate = getExchangeRate(from, to);
    return rate.doubleValue() * amount;
  }

  public Map<String, Double> convertToMultipleCurrencies(
      String from, Double amount, List<String> currencies) {
    Map<String, Number> rates = getAllExchangeRates(from);
    Map<String, Double> conversions = new HashMap<>();
    for (String currency : currencies) {
      conversions.put(currency, rates.get(currency).doubleValue() * amount);
    }
    return conversions;
  }
}
