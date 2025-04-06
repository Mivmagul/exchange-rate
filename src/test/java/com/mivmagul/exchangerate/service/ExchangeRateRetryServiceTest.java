package com.mivmagul.exchangerate.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mivmagul.exchangerate.dto.CurrencyRate;
import com.mivmagul.exchangerate.provider.ExchangeRateProvider;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
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

    Set<CurrencyRate> mockRates =
        Set.of(
            new CurrencyRate("USD", BigDecimal.valueOf(1.3)),
            new CurrencyRate("EUR", BigDecimal.valueOf(0.87)));
    when(exchangeRateHostProvider.fetchRates("GBP"))
        .thenThrow(new RuntimeException("Exchange Rate Host failed"))
        .thenReturn(mockRates);

    // execute
    Set<CurrencyRate> response = retryService.fetchRatesForBaseCurrency("GBP");

    // verify
    assertEquals(2, response.size());
    assertTrue(
        response.stream()
            .anyMatch(
                rate ->
                    rate.getCurrencyCode().equals("USD")
                        && rate.getRate().compareTo(BigDecimal.valueOf(1.3)) == 0));
    assertTrue(
        response.stream()
            .anyMatch(
                rate ->
                    rate.getCurrencyCode().equals("EUR")
                        && rate.getRate().compareTo(BigDecimal.valueOf(0.87)) == 0));

    verify(fixerIoProvider, times(2)).fetchRates("GBP");
    verify(exchangeRateHostProvider, times(2)).fetchRates("GBP");
  }

  @Test
  public void testFallbackToSecondProviderWithinOneAttempt() {
    // setup
    when(fixerIoProvider.fetchRates("GBP")).thenThrow(new RuntimeException("Fixer IO failed"));

    Set<CurrencyRate> mockRates =
        Set.of(
            new CurrencyRate("USD", BigDecimal.valueOf(1.3)),
            new CurrencyRate("EUR", BigDecimal.valueOf(0.87)));
    when(exchangeRateHostProvider.fetchRates("GBP")).thenReturn(mockRates);

    // execute
    Set<CurrencyRate> response = retryService.fetchRatesForBaseCurrency("GBP");

    // verify
    assertEquals(2, response.size());
    assertTrue(
        response.stream()
            .anyMatch(
                rate ->
                    rate.getCurrencyCode().equals("USD")
                        && rate.getRate().compareTo(BigDecimal.valueOf(1.3)) == 0));
    assertTrue(
        response.stream()
            .anyMatch(
                rate ->
                    rate.getCurrencyCode().equals("EUR")
                        && rate.getRate().compareTo(BigDecimal.valueOf(0.87)) == 0));

    verify(fixerIoProvider).fetchRates("GBP");
    verify(exchangeRateHostProvider).fetchRates("GBP");
  }
}
