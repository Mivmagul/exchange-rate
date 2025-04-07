package com.mivmagul.exchangerate.provider;

import org.springframework.web.client.RestTemplate;

public class FixerExchangeRateProvider extends AbstractExchangeRateProvider {

  public FixerExchangeRateProvider(RestTemplate restTemplate, String endpoint, String accessKey) {
    super(restTemplate, endpoint, accessKey);
  }

  @Override
  protected String buildUrl(String source) {
    return String.format("%s?access_key=%s&base=%s", endpoint, accessKey, source);
  }
}
