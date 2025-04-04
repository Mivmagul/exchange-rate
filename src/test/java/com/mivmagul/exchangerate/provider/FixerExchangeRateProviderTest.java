package com.mivmagul.exchangerate.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class FixerExchangeRateProviderTest {

  @Mock private RestTemplate restTemplate;

  private FixerExchangeRateProvider provider;

  @BeforeEach
  public void setup() {
    provider =
        new FixerExchangeRateProvider(
            restTemplate, "https://data.fixer.io/api/latest", "test-access-key");
  }

  @Test
  public void testFetchRates() {
    // setup
    String sourceCurrency = "USD";
    String expectedUrl = "https://data.fixer.io/api/latest?access_key=test-access-key&base=USD";
    Map<String, Object> mockResponse = Map.of("rates", Map.of("EUR", 0.85, "GBP", 0.75));

    when(restTemplate.getForObject(Mockito.eq(expectedUrl), Mockito.eq(Map.class)))
        .thenReturn(mockResponse);

    // execute
    Map<String, Object> response = provider.fetchRates(sourceCurrency);

    // verify
    assertEquals(mockResponse, response);
  }
}
