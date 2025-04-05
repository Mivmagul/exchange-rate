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
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@ExtendWith(MockitoExtension.class)
public class ExchangeRateRetryServiceTest {

  @Mock private ExchangeRateProvider fixerIoProvider;
  @Mock private ExchangeRateProvider exchangeRateHostProvider;
  private ExchangeRateRetryService retryService;

  @BeforeEach
  public void setup() {
    ExchangeRateRetryService originalRetryService =
        new ExchangeRateRetryService(List.of(fixerIoProvider, exchangeRateHostProvider));
    ProxyFactory proxyFactory = new ProxyFactory(originalRetryService);
    proxyFactory.addAdvice(new org.springframework.retry.interceptor.RetryOperationsInterceptor());
    retryService = (ExchangeRateRetryService) proxyFactory.getProxy();
  }

  @Test
  public void testFetchRatesWithRetry() {
    // setup
    when(fixerIoProvider.fetchRates("GBP")).thenThrow(new RuntimeException("Fixer IO failed"));
    when(exchangeRateHostProvider.fetchRates("GBP"))
        .thenThrow(new RuntimeException("Exchange Rate Host failed"))
        .thenReturn(Map.of("rates", Map.of("USD", 1.3, "EUR", 0.87)));

    // execute
    Map<String, BigDecimal> rates = retryService.fetchRatesForBaseCurrency("GBP");

    // verify
    assertEquals(BigDecimal.valueOf(1.3), rates.get("USD"));
    assertEquals(BigDecimal.valueOf(0.87), rates.get("EUR"));

    verify(fixerIoProvider, times(2)).fetchRates("GBP");
    verify(exchangeRateHostProvider, times(2)).fetchRates("GBP");
  }

  @Test
  public void testFallbackToSecondProviderWithinOneAttempt() {
    // setup
    when(fixerIoProvider.fetchRates("GBP")).thenThrow(new RuntimeException("Fixer IO failed"));
    when(exchangeRateHostProvider.fetchRates("GBP"))
        .thenReturn(Map.of("rates", Map.of("USD", 1.3, "EUR", 0.87)));

    // execute
    Map<String, BigDecimal> rates = retryService.fetchRatesForBaseCurrency("GBP");

    // verify
    assertEquals(BigDecimal.valueOf(1.3), rates.get("USD"));
    assertEquals(BigDecimal.valueOf(0.87), rates.get("EUR"));

    verify(fixerIoProvider, times(1)).fetchRates("GBP");
    verify(exchangeRateHostProvider, times(1)).fetchRates("GBP");
  }

  @Test
  public void testProviderReturnsInvalidData() {
    // setup
    when(fixerIoProvider.fetchRates("GBP"))
        .thenReturn(Map.of("invalidKey", Map.of("USD", 1.2, "EUR", 0.85)));
    when(exchangeRateHostProvider.fetchRates("GBP"))
        .thenReturn(Map.of("rates", Map.of("USD", 1.3, "EUR", 0.87)));

    // execute
    Map<String, BigDecimal> rates = retryService.fetchRatesForBaseCurrency("GBP");

    // verify
    assertEquals(BigDecimal.valueOf(1.3), rates.get("USD"));
    assertEquals(BigDecimal.valueOf(0.87), rates.get("EUR"));

    verify(fixerIoProvider, times(1)).fetchRates("GBP");
    verify(exchangeRateHostProvider, times(1)).fetchRates("GBP");
  }
}
