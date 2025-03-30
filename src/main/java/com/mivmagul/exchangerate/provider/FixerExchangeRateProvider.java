package com.mivmagul.exchangerate.provider;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("fixerProvider")
public class FixerExchangeRateProvider implements ExchangeRateProvider {
  private static final Logger logger = LoggerFactory.getLogger(FixerExchangeRateProvider.class);

  private final RestTemplate restTemplate;

  @Value("${exchange-rate.fixer.api.endpoint}")
  private String endpoint;

  @Value("${exchange-rate.fixer.api.access-key}")
  private String accessKey;

  public FixerExchangeRateProvider(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Cacheable(value = "exchangeRates", key = "#source")
  @Override
  public Map<String, Object> fetchRates(String source) {
    logger.debug("Fetching exchange rates from fixer.io for currency: {}", source);
    String url = String.format("%s?access_key=%s&base=%s", endpoint, accessKey, source);
    return restTemplate.getForObject(url, Map.class);
  }
}
