package com.mivmagul.exchangerate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.mivmagul.exchangerate.data.ExchangeRateProviderType;
import com.mivmagul.exchangerate.provider.ExchangeRateProvider;
import com.mivmagul.exchangerate.provider.ExchangeRateProviderFactory;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {

  @Mock private ExchangeRateProviderFactory providerFactory;

  @Mock private ExchangeRateProvider provider;

  private ExchangeRateService service;

  @BeforeEach
  public void setup() {
    when(providerFactory.getProvider(ExchangeRateProviderType.FIXER_IO)).thenReturn(provider);

    service = new ExchangeRateService(providerFactory, ExchangeRateProviderType.FIXER_IO);
  }

  @Test
  public void testGetExchangeRate() {
    // setup
    Map<String, Object> mockResponse = Map.of("rates", Map.of("USD", 1.2, "EUR", 0.85));
    when(provider.fetchRates("GBP")).thenReturn(mockResponse);

    // execute
    Number rate = service.getExchangeRate("GBP", "USD");

    // verify
    assertEquals(1.2, rate);
  }

  @Test
  public void testGetAllExchangeRates() {
    // setup
    Map<String, Object> mockResponse = Map.of("rates", Map.of("USD", 1.2, "EUR", 0.85));
    when(provider.fetchRates("GBP")).thenReturn(mockResponse);

    // execute
    Map<String, Number> rates = service.getAllExchangeRates("GBP");

    // verify
    assertEquals(1.2, rates.get("USD"));
    assertEquals(0.85, rates.get("EUR"));
  }

  @Test
  public void testConvertValue() {
    // setup
    Map<String, Object> mockResponse = Map.of("rates", Map.of("USD", 1.2));
    when(provider.fetchRates("GBP")).thenReturn(mockResponse);

    // execute
    Number convertedValue = service.convertValue("GBP", "USD", 100.0);

    // verify
    assertEquals(120.0, convertedValue);
  }

  @Test
  public void testConvertToMultipleCurrencies() {
    // setup
    Map<String, Object> mockResponse = Map.of("rates", Map.of("USD", 1.2, "EUR", 0.85));
    when(provider.fetchRates("GBP")).thenReturn(mockResponse);

    // execute
    Map<String, Double> conversions =
        service.convertToMultipleCurrencies("GBP", 100.0, List.of("USD", "EUR"));

    // verify
    assertEquals(120.0, conversions.get("USD"));
    assertEquals(85.0, conversions.get("EUR"));
  }
}
