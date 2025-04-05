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
  @Mock private ExchangeRateRetryService retryService;
  private ExchangeRateService service;

  @BeforeEach
  public void setup() {
    Map<String, Object> fixerIoRates = Map.of("rates", Map.of("USD", 1.2, "EUR", 0.85));
    Map<String, Object> exchangeRateHostRates = Map.of("rates", Map.of("USD", 1.3, "EUR", 0.87));

    lenient().when(fixerIoProvider.fetchRates("GBP")).thenReturn(fixerIoRates);
    lenient().when(exchangeRateHostProvider.fetchRates("GBP")).thenReturn(exchangeRateHostRates);

    service = new ExchangeRateService(retryService, "GBP", 2);
  }

  @Test
  public void testFetchRatesForBaseCurrency() {
    // setup
    Map<String, BigDecimal> rates =
        Map.of("USD", BigDecimal.valueOf(1.2), "EUR", BigDecimal.valueOf(0.85));
    when(retryService.fetchRatesForBaseCurrency("GBP")).thenReturn(rates);

    // execute
    Map<String, BigDecimal> result = service.getAllExchangeRates("GBP");

    // verify
    assertEquals(BigDecimal.valueOf(1.2), result.get("USD"));
    assertEquals(BigDecimal.valueOf(0.85), result.get("EUR"));
  }

  @Test
  public void testGetExchangeRate() {
    // setup
    Map<String, BigDecimal> rates =
        Map.of("USD", BigDecimal.valueOf(1.2), "EUR", BigDecimal.valueOf(0.85));
    when(retryService.fetchRatesForBaseCurrency("GBP")).thenReturn(rates);

    // execute
    BigDecimal rate = service.getExchangeRate("GBP", "USD");

    // verify
    assertEquals(BigDecimal.valueOf(1.2), rate);
  }

  @Test
  public void testGetAllExchangeRates() {
    // setup
    Map<String, BigDecimal> rates =
        Map.of("USD", BigDecimal.valueOf(1.2), "EUR", BigDecimal.valueOf(0.85));
    when(retryService.fetchRatesForBaseCurrency("GBP")).thenReturn(rates);

    // execute
    Map<String, BigDecimal> result = service.getAllExchangeRates("GBP");

    // verify
    assertEquals(BigDecimal.valueOf(1.2), result.get("USD"));
    assertEquals(BigDecimal.valueOf(0.85), result.get("EUR"));
  }

  @Test
  public void testConvertValue() {
    // setup
    Map<String, BigDecimal> rates =
        Map.of("USD", BigDecimal.valueOf(1.2), "EUR", BigDecimal.valueOf(0.85));
    when(retryService.fetchRatesForBaseCurrency("GBP")).thenReturn(rates);

    // execute
    BigDecimal convertedValue = service.convertValue("GBP", "USD", BigDecimal.valueOf(100));

    // verify
    assertEquals(0, BigDecimal.valueOf(120).compareTo(convertedValue));
  }

  @Test
  public void testConvertToMultipleCurrencies() {
    // setup
    Map<String, BigDecimal> rates =
        Map.of("USD", BigDecimal.valueOf(1.2), "EUR", BigDecimal.valueOf(0.85));
    when(retryService.fetchRatesForBaseCurrency("GBP")).thenReturn(rates);

    // execute
    Map<String, BigDecimal> conversions =
        service.convertToMultipleCurrencies("GBP", BigDecimal.valueOf(100), List.of("USD", "EUR"));

    // verify
    assertEquals(0, BigDecimal.valueOf(120).compareTo(conversions.get("USD")));
    assertEquals(0, BigDecimal.valueOf(85).compareTo(conversions.get("EUR")));
  }
}
