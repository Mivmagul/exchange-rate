package com.mivmagul.exchangerate.provider;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("hostProvider")
public class ExchangeRateHostProvider implements ExchangeRateProvider {
  private static final Logger logger = LoggerFactory.getLogger(ExchangeRateHostProvider.class);

  private final RestTemplate restTemplate;

  @Value("${exchange-rate.host.api.endpoint}")
  private String endpoint;

  @Value("${exchange-rate.host.api.access-key}")
  private String accessKey;

  public ExchangeRateHostProvider(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Cacheable(value = "exchangeRates", key = "#source")
  @Override
  public Map<String, Object> fetchRates(String source) {
    logger.debug("Fetching exchange rates from exchangerate.host for currency: {}", source);
    String url = String.format("%s?access_key=%s?source=%s", endpoint, accessKey, source);
    return restTemplate.getForObject(url, Map.class);
  }
}
