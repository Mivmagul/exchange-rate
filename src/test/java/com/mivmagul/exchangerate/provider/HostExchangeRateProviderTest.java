package com.mivmagul.exchangerate.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.mivmagul.exchangerate.dto.CurrencyRate;
import com.mivmagul.exchangerate.dto.ExchangeRateResponse;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class HostExchangeRateProviderTest {

  @Mock private RestTemplate restTemplate;

  private HostExchangeRateProvider provider;

  @BeforeEach
  public void setup() {
    provider =
        new HostExchangeRateProvider(
            restTemplate, "https://api.exchangerate.host/live", "test-access-key");
  }

  @Test
  public void testFetchRates() {
    // setup
    String sourceCurrency = "USD";
    String expectedUrl = "https://api.exchangerate.host/live?access_key=test-access-key&source=USD";

    ExchangeRateResponse mockResponse = new ExchangeRateResponse();
    mockResponse.setBaseCurrency("USD");
    mockResponse.setRates(
        Map.of(
            "EUR", BigDecimal.valueOf(0.85),
            "GBP", BigDecimal.valueOf(0.75)));

    when(restTemplate.getForObject(Mockito.eq(expectedUrl), Mockito.eq(ExchangeRateResponse.class)))
        .thenReturn(mockResponse);

    Set<CurrencyRate> expectedRates =
        Set.of(
            new CurrencyRate("EUR", BigDecimal.valueOf(0.85)),
            new CurrencyRate("GBP", BigDecimal.valueOf(0.75)));

    // execute
    Set<CurrencyRate> actualRates = provider.fetchRates(sourceCurrency);

    // verify
    assertEquals(expectedRates, actualRates);
  }
}
