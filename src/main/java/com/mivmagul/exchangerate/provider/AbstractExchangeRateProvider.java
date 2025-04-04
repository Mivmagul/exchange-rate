package com.mivmagul.exchangerate.provider;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractExchangeRateProvider implements ExchangeRateProvider {
  private static final Logger logger = LoggerFactory.getLogger(AbstractExchangeRateProvider.class);

  protected final RestTemplate restTemplate;
  protected final String endpoint;
  protected final String accessKey;

  public AbstractExchangeRateProvider(
      RestTemplate restTemplate, String endpoint, String accessKey) {
    this.restTemplate = restTemplate;
    this.endpoint = endpoint;
    this.accessKey = accessKey;
  }

  @Cacheable(value = "exchangeRates", key = "#source")
  @Override
  public Map<String, Object> fetchRates(String source) {
    logger.debug(
        "Fetching exchange rates from {} for currency: {}", getClass().getSimpleName(), source);
    String url = buildUrl(source);
    try {
      return restTemplate.getForObject(url, Map.class);
    } catch (Exception exception) {
      logger.error("Failed to fetch rates from {}", getClass().getSimpleName(), exception);
      throw new RuntimeException(getClass().getSimpleName() + " provider failed", exception);
    }
  }

  protected abstract String buildUrl(String source);
}
