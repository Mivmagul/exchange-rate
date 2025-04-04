package com.mivmagul.exchangerate.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mivmagul.exchangerate.provider.ExchangeRateProvider;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {

  @Mock private ExchangeRateProvider fixerIoProvider;

  @Mock private ExchangeRateProvider exchangeRateHostProvider;

  private ExchangeRateService service;

  @BeforeEach
  public void setup() {
    Map<String, Object> fixerIoRates = Map.of("rates", Map.of("USD", 1.2, "EUR", 0.85));
    Map<String, Object> exchangeRateHostRates = Map.of("rates", Map.of("USD", 1.3, "EUR", 0.87));

    lenient().when(fixerIoProvider.fetchRates("GBP")).thenReturn(fixerIoRates);
    lenient().when(exchangeRateHostProvider.fetchRates("GBP")).thenReturn(exchangeRateHostRates);

    service = new ExchangeRateService(List.of(fixerIoProvider, exchangeRateHostProvider), "GBP", 2);
  }

  @Test
  public void testFetchRatesForBaseCurrency() {
    // execute
    Map<String, BigDecimal> rates = service.fetchRatesForBaseCurrency("GBP");

    // verify
    assertEquals(BigDecimal.valueOf(1.2), rates.get("USD"));
    assertEquals(BigDecimal.valueOf(0.85), rates.get("EUR"));
  }

  @Test
  public void testFetchRatesForBaseCurrencyWithNull() {
    // execute
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              service.fetchRatesForBaseCurrency(null);
            });

    // verify
    assertEquals("Base currency cannot be null", exception.getMessage());
  }

  @Test
  public void testGetExchangeRate() {
    // execute
    BigDecimal rate = service.getExchangeRate("GBP", "USD");

    // verify
    assertEquals(BigDecimal.valueOf(1.2), rate);
  }

  @Test
  public void testGetAllExchangeRates() {
    // execute
    Map<String, BigDecimal> rates = service.getAllExchangeRates("GBP");

    // verify
    assertEquals(BigDecimal.valueOf(1.2), rates.get("USD"));
    assertEquals(BigDecimal.valueOf(0.85), rates.get("EUR"));
  }

  @Test
  public void testConvertValue() {
    // execute
    BigDecimal convertedValue = service.convertValue("GBP", "USD", BigDecimal.valueOf(100));

    // verify
    assertEquals(0, BigDecimal.valueOf(120).compareTo(convertedValue));
  }

  @Test
  public void testConvertToMultipleCurrencies() {
    // execute
    Map<String, BigDecimal> conversions =
        service.convertToMultipleCurrencies("GBP", BigDecimal.valueOf(100), List.of("USD", "EUR"));

    // verify
    assertEquals(0, BigDecimal.valueOf(120).compareTo(conversions.get("USD")));
    assertEquals(0, BigDecimal.valueOf(85).compareTo(conversions.get("EUR")));
  }

  @Test
  public void testFallbackToSecondProvider() {
    // setup
    when(fixerIoProvider.fetchRates("GBP")).thenThrow(new RuntimeException("Provider failed"));

    // execute
    Map<String, BigDecimal> rates = service.fetchRatesForBaseCurrency("GBP");

    // verify
    assertEquals(BigDecimal.valueOf(1.3), rates.get("USD"));
    assertEquals(BigDecimal.valueOf(0.87), rates.get("EUR"));
  }

  @Test
  public void testAllProvidersFail() {
    // setup
    when(fixerIoProvider.fetchRates("GBP")).thenThrow(new RuntimeException("Fixer IO failed"));
    when(exchangeRateHostProvider.fetchRates("GBP"))
        .thenThrow(new RuntimeException("Exchange Rate Host failed"));

    // execute
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              service.fetchRatesForBaseCurrency("GBP");
            });

    // verify
    assertEquals("All providers failed to fetch exchange rates.", exception.getMessage());
  }
}
