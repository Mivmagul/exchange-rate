package com.mivmagul.exchangerate.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mivmagul.exchangerate.dto.CurrencyRate;
import com.mivmagul.exchangerate.service.ExchangeRateService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ExchangeRateController.class)
public class ExchangeRateControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ExchangeRateService exchangeRateService;

  @BeforeEach
  public void setup() {
    Mockito.reset(exchangeRateService);
  }

  @Test
  public void testGetExchangeRate() throws Exception {
    // setup
    String from = "GBP";
    String to = "USD";
    BigDecimal mockRate = BigDecimal.valueOf(1.2);

    when(exchangeRateService.getExchangeRate(from, to)).thenReturn(mockRate);

    // execute & verify
    mockMvc
        .perform(get("/api/exchange-rates/{from}/{to}", from, to))
        .andExpect(status().isOk())
        .andExpect(content().string(mockRate.toString()));
  }

  @Test
  public void testGetAllExchangeRates() throws Exception {
    // setup
    String from = "GBP";
    Set<CurrencyRate> mockRates =
        Set.of(
            new CurrencyRate("USD", BigDecimal.valueOf(1.2)),
            new CurrencyRate("EUR", BigDecimal.valueOf(0.85)));

    when(exchangeRateService.getAllExchangeRates(from)).thenReturn(mockRates);

    // execute & verify
    mockMvc
        .perform(get("/api/exchange-rates/{from}", from))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.currencyCode == 'USD')].rate").value(1.2))
        .andExpect(jsonPath("$[?(@.currencyCode == 'EUR')].rate").value(0.85));
  }

  @Test
  public void testConvertValue() throws Exception {
    // setup
    String from = "GBP";
    String to = "USD";
    BigDecimal amount = BigDecimal.valueOf(100);
    BigDecimal mockConvertedValue = BigDecimal.valueOf(120);

    when(exchangeRateService.convertValue(from, to, amount)).thenReturn(mockConvertedValue);

    // execute & verify
    mockMvc
        .perform(
            get("/api/exchange-rates/{from}/{to}/conversion", from, to)
                .param("amount", amount.toString()))
        .andExpect(status().isOk())
        .andExpect(content().string(mockConvertedValue.toString()));
  }

  @Test
  public void testConvertToMultipleCurrencies() throws Exception {
    // setup
    String from = "GBP";
    BigDecimal amount = BigDecimal.valueOf(100);
    List<String> currencies = List.of("USD", "EUR");
    Set<CurrencyRate> mockConversions =
        Set.of(
            new CurrencyRate("USD", BigDecimal.valueOf(120)),
            new CurrencyRate("EUR", BigDecimal.valueOf(85)));

    when(exchangeRateService.convertToMultipleCurrencies(from, amount, currencies))
        .thenReturn(mockConversions);

    // execute & verify
    mockMvc
        .perform(
            get("/api/exchange-rates/{from}/conversion", from)
                .param("amount", amount.toString())
                .param("currencies", "USD", "EUR"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.currencyCode == 'USD')].rate").value(120))
        .andExpect(jsonPath("$[?(@.currencyCode == 'EUR')].rate").value(85));
  }

  @Test
  public void testValidateFromCurrency() throws Exception {
    // execute & verify
    mockMvc
        .perform(get("/api/exchange-rates/{from}/{to}", "USD", "EUR"))
        .andExpect(status().isBadRequest());
  }
}
