package com.mivmagul.exchangerate.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateService {

  @Value("${exchange-rate.api.url}")
  private String apiUrl;

  @Value("${exchange-rate.api.access-key}")
  private String accessKey;

  private final RestTemplate restTemplate;

  public ExchangeRateService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Cacheable("exchangeRates")
  public Map<String, Object> fetchRates(String base) {
    String url = String.format("%s?access_key=%s&base=%s", apiUrl, accessKey, base);
    return restTemplate.getForObject(url, Map.class);
  }

  public Number getExchangeRate(String from, String to) {
    Map<String, Object> response = fetchRates(from);
    Map<String, Number> rates = (Map<String, Number>) response.get("rates");
    return rates.get(to);
  }

  public Map<String, Number> getAllExchangeRates(String from) {
    Map<String, Object> response = fetchRates(from);
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
