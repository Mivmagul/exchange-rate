package com.mivmagul.exchangerate.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mivmagul.exchangerate.service.ExchangeRateService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
    Number mockRate = 1.2;

    when(exchangeRateService.getExchangeRate(from, to)).thenReturn(mockRate);

    // execute & verify
    mockMvc
        .perform(get("/api/exchange/rate/{from}/{to}", from, to))
        .andExpect(status().isOk())
        .andExpect(content().string(mockRate.toString()));
  }

  @Test
  public void testGetAllExchangeRates() throws Exception {
    // setup
    String from = "GBP";
    Map<String, Number> mockRates = Map.of("USD", 1.2, "EUR", 0.85);

    when(exchangeRateService.getAllExchangeRates(from)).thenReturn(mockRates);

    // execute & verify
    mockMvc
        .perform(get("/api/exchange/rates/{from}", from))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.USD").value(1.2))
        .andExpect(jsonPath("$.EUR").value(0.85));
  }

  @Test
  public void testConvertValue() throws Exception {
    // setup
    String from = "GBP";
    String to = "USD";
    BigDecimal amount = BigDecimal.valueOf(100);
    Number mockConvertedValue = 120;

    when(exchangeRateService.convertValue(from, to, amount)).thenReturn(mockConvertedValue);

    // execute & verify
    mockMvc
        .perform(
            get("/api/exchange/convert/{from}/{to}", from, to).param("amount", amount.toString()))
        .andExpect(status().isOk())
        .andExpect(content().string(mockConvertedValue.toString()));
  }

  @Test
  public void testConvertToMultipleCurrencies() throws Exception {
    // setup
    String from = "GBP";
    BigDecimal amount = BigDecimal.valueOf(100);
    List<String> currencies = List.of("USD", "EUR");
    Map<String, BigDecimal> mockConversions =
        Map.of("USD", BigDecimal.valueOf(120), "EUR", BigDecimal.valueOf(85));

    when(exchangeRateService.convertToMultipleCurrencies(from, amount, currencies))
        .thenReturn(mockConversions);

    // execute & verify
    mockMvc
        .perform(
            get("/api/exchange/convert/{from}", from)
                .param("amount", amount.toString())
                .param("currencies", "USD", "EUR"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.USD").value(120))
        .andExpect(jsonPath("$.EUR").value(85));
  }

  @Test
  public void testValidateFromCurrency() throws Exception {
    // execute & verify
    mockMvc
        .perform(get("/api/exchange/rate/{from}/{to}", "USD", "EUR"))
        .andExpect(status().isBadRequest());
  }
}
