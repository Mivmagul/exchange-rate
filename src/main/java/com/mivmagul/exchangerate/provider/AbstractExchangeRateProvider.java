package com.mivmagul.exchangerate.provider;

import com.mivmagul.exchangerate.dto.CurrencyRate;
import com.mivmagul.exchangerate.dto.ExchangeRateResponse;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestTemplate;

@Slf4j
public abstract class AbstractExchangeRateProvider implements ExchangeRateProvider {
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
  public Set<CurrencyRate> fetchRates(String source) {
    log.debug(
        "Fetching exchange rates from {} for currency: {}", getClass().getSimpleName(), source);

    String url = buildUrl(source);

    try {
      ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);
      if (response == null
          || response.getBaseCurrency() == null
          || response.getRates() == null
          || response.getRates().isEmpty()) {
        throw new RuntimeException("Invalid response from " + getClass().getSimpleName());
      }
      return response.getRates().entrySet().stream()
          .map(entry -> new CurrencyRate(entry.getKey(), entry.getValue()))
          .collect(Collectors.toSet());
    } catch (Exception exception) {
      log.error("Failed to fetch rates from {}", getClass().getSimpleName(), exception);
      throw new RuntimeException(getClass().getSimpleName() + " provider failed", exception);
    }
  }

  protected abstract String buildUrl(String source);
}
