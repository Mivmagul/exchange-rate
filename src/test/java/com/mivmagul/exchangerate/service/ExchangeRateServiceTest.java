package com.mivmagul.exchangerate.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mivmagul.exchangerate.dto.CurrencyRate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {
  @Mock private ExchangeRateRetryService retryService;

  private ExchangeRateService service;

  @BeforeEach
  public void setup() {
    service = new ExchangeRateService(retryService, "GBP", 2);
  }

  @Test
  public void testFetchRatesForBaseCurrency() {
    // setup
    Set<CurrencyRate> mockRates =
        Set.of(
            new CurrencyRate("USD", BigDecimal.valueOf(1.2)),
            new CurrencyRate("EUR", BigDecimal.valueOf(0.85)),
            new CurrencyRate("GBP", BigDecimal.valueOf(1.0)));

    when(retryService.fetchRatesForBaseCurrency("GBP")).thenReturn(mockRates);

    // execute
    Set<CurrencyRate> result = service.getAllExchangeRates("GBP");

    // verify
    assertEquals(3, result.size());
    assertTrue(
        result.stream()
            .anyMatch(
                rate ->
                    rate.getCurrencyCode().equals("USD")
                        && rate.getRate().compareTo(BigDecimal.valueOf(1.2)) == 0));
    assertTrue(
        result.stream()
            .anyMatch(
                rate ->
                    rate.getCurrencyCode().equals("EUR")
                        && rate.getRate().compareTo(BigDecimal.valueOf(0.85)) == 0));
    assertTrue(
        result.stream()
            .anyMatch(
                rate ->
                    rate.getCurrencyCode().equals("GBP")
                        && rate.getRate().compareTo(BigDecimal.valueOf(1.0)) == 0));
  }

  @Test
  public void testGetExchangeRate() {
    // setup
    Set<CurrencyRate> mockRates =
        Set.of(
            new CurrencyRate("USD", BigDecimal.valueOf(1.2)),
            new CurrencyRate("EUR", BigDecimal.valueOf(0.85)),
            new CurrencyRate("GBP", BigDecimal.valueOf(1.0)));

    when(retryService.fetchRatesForBaseCurrency("GBP")).thenReturn(mockRates);

    // execute
    BigDecimal rate = service.getExchangeRate("GBP", "USD");

    // verify
    assertEquals(BigDecimal.valueOf(1.2), rate);
  }

  @Test
  public void testConvertValue() {
    // setup
    Set<CurrencyRate> mockRates =
        Set.of(
            new CurrencyRate("USD", BigDecimal.valueOf(1.2)),
            new CurrencyRate("EUR", BigDecimal.valueOf(0.85)),
            new CurrencyRate("GBP", BigDecimal.valueOf(1.0)));

    when(retryService.fetchRatesForBaseCurrency("GBP")).thenReturn(mockRates);

    // execute
    BigDecimal convertedValue = service.convertValue("GBP", "USD", BigDecimal.valueOf(100));

    // verify
    assertEquals(0, BigDecimal.valueOf(120).compareTo(convertedValue));
  }

  @Test
  public void testConvertToMultipleCurrencies() {
    // setup
    Set<CurrencyRate> mockRates =
        Set.of(
            new CurrencyRate("USD", BigDecimal.valueOf(1.2)),
            new CurrencyRate("EUR", BigDecimal.valueOf(0.85)),
            new CurrencyRate("GBP", BigDecimal.valueOf(1.0)));

    when(retryService.fetchRatesForBaseCurrency("GBP")).thenReturn(mockRates);

    List<String> targetCurrencies = List.of("USD", "EUR");

    // execute
    Set<CurrencyRate> conversions =
        service.convertToMultipleCurrencies("GBP", BigDecimal.valueOf(100), targetCurrencies);

    // verify
    assertTrue(
        conversions.stream()
            .anyMatch(
                rate ->
                    rate.getCurrencyCode().equals("USD")
                        && rate.getRate().compareTo(BigDecimal.valueOf(120)) == 0));
    assertTrue(
        conversions.stream()
            .anyMatch(
                rate ->
                    rate.getCurrencyCode().equals("EUR")
                        && rate.getRate().compareTo(BigDecimal.valueOf(85)) == 0));
  }
}
