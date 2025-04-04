package com.mivmagul.exchangerate.controller;

import com.mivmagul.exchangerate.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeRateController {

  private final ExchangeRateService exchangeRateService;

  public ExchangeRateController(ExchangeRateService exchangeRateService) {
    this.exchangeRateService = exchangeRateService;
  }

  @Operation(
      summary = "Get exchange rate between two currencies",
      description =
          """
            Returns the exchange rate from the specified source currency (from)
            to the target currency (currencyTo).
            """)
  @GetMapping("/rate/{currencyFrom}/{currencyTo}")
  public BigDecimal getExchangeRate(
      @PathVariable String currencyFrom, @PathVariable String currencyTo) {
    validateFromCurrency(currencyFrom);
    return exchangeRateService.getExchangeRate(
        currencyFrom.toUpperCase(), currencyTo.toUpperCase());
  }

  @Operation(
      summary = "Get all exchange rates for a currency",
      description =
          """
            Returns all exchange rates for the specified source currency (currencyFrom)
            to all available target currencies.
            """)
  @GetMapping("/rates/{currencyFrom}")
  public Map<String, BigDecimal> getAllExchangeRates(@PathVariable String currencyFrom) {
    validateFromCurrency(currencyFrom);
    return exchangeRateService.getAllExchangeRates(currencyFrom.toUpperCase());
  }

  @Operation(
      summary = "Convert value between two currencies",
      description =
          """
            Converts the specified amount from the source currency (currencyFrom)
            to the target currency (currencyTo) using the current exchange rate.
            """)
  @GetMapping("/convert/{currencyFrom}/{currencyTo}")
  public BigDecimal convertValue(
      @PathVariable String currencyFrom,
      @PathVariable String currencyTo,
      @RequestParam BigDecimal amount) {
    validateFromCurrency(currencyFrom);
    return exchangeRateService.convertValue(
        currencyFrom.toUpperCase(), currencyTo.toUpperCase(), amount);
  }

  @Operation(
      summary = "Convert value to multiple currencies",
      description =
          """
            Converts the specified amount from the source currency (currencyFrom)
            to multiple target currencies.
            The list of target currencies is provided as a parameter.
            """)
  @GetMapping("/convert/{currencyFrom}")
  public Map<String, BigDecimal> convertToMultipleCurrencies(
      @PathVariable String currencyFrom,
      @RequestParam BigDecimal amount,
      @RequestParam List<String> currencies) {
    validateFromCurrency(currencyFrom);
    List<String> currenciesUpperCase = currencies.stream().map(String::toUpperCase).toList();
    return exchangeRateService.convertToMultipleCurrencies(
        currencyFrom.toUpperCase(), amount, currenciesUpperCase);
  }

  private void validateFromCurrency(String currencyFrom) {
    if ("USD".equalsIgnoreCase(currencyFrom)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          """
              'USD' cannot be used as the source currency (currencyFrom).
              Please choose another currency (e.g. EUR).
              """);
    }
  }
}
