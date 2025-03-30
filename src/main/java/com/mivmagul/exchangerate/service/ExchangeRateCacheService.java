package com.mivmagul.exchangerate.service;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateCacheService {
  private static final Logger logger = LoggerFactory.getLogger(ExchangeRateCacheService.class);

  private final RestTemplate restTemplate;

  @Value("${exchange-rate.api.url}")
  private String apiUrl;

  @Value("${exchange-rate.api.access-key}")
  private String accessKey;

  public ExchangeRateCacheService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Cacheable(value = "exchangeRates", key = "#base")
  public Map<String, Object> fetchRates(String base) {
    logger.debug("Fetching exchange rates from external provider for currency: {}", base);
    String url = String.format("%s?access_key=%s&base=%s", apiUrl, accessKey, base);
    return restTemplate.getForObject(url, Map.class);
  }
}
