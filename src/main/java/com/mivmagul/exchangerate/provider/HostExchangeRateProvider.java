package com.mivmagul.exchangerate.provider;

import org.springframework.web.client.RestTemplate;

public class HostExchangeRateProvider extends AbstractExchangeRateProvider {

  public HostExchangeRateProvider(RestTemplate restTemplate, String endpoint, String accessKey) {
    super(restTemplate, endpoint, accessKey);
  }

  @Override
  protected String buildUrl(String source) {
    return String.format("%s?access_key=%s&source=%s", endpoint, accessKey, source);
  }
}
