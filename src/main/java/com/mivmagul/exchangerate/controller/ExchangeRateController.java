package com.mivmagul.exchangerate.controller;

import com.mivmagul.exchangerate.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @Operation(
            summary = "Get exchange rate between two currencies",
            description = "Returns the exchange rate from the specified source currency (from) to the target currency (to)."
    )
    @GetMapping("/rate/{from}/{to}")
    public Number getExchangeRate(@PathVariable String from, @PathVariable String to) {
        validateFromCurrency(from);
        return exchangeRateService.getExchangeRate(from, to);
    }

    @Operation(
            summary = "Get all exchange rates for a currency",
            description = "Returns all exchange rates for the specified source currency (from) to all available target currencies."
    )
    @GetMapping("/rates/{from}")
    public Map<String, Number> getAllExchangeRates(@PathVariable String from) {
        validateFromCurrency(from);
        return exchangeRateService.getAllExchangeRates(from);
    }

    @Operation(
            summary = "Convert value between two currencies",
            description = "Converts the specified amount from the source currency (from) to the target currency (to) using the current exchange rate."
    )
    @GetMapping("/convert/{from}/{to}")
    public Number convertValue(@PathVariable String from, @PathVariable String to, @RequestParam Double amount) {
        validateFromCurrency(from);
        return exchangeRateService.convertValue(from, to, amount);
    }

    @Operation(
            summary = "Convert value to multiple currencies",
            description = "Converts the specified amount from the source currency (from) to multiple target currencies. The list of target currencies is provided as a parameter."
    )
    @GetMapping("/convert/{from}")
    public Map<String, Double> convertToMultipleCurrencies(@PathVariable String from, @RequestParam Double amount, @RequestParam List<String> currencies) {
        validateFromCurrency(from);
        return exchangeRateService.convertToMultipleCurrencies(from, amount, currencies);
    }

    private void validateFromCurrency(String from) {
        if ("USD".equalsIgnoreCase(from)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "'USD' cannot be used as the source currency (from). Please choose another currency (e.g. EUR)."
            );
        }
    }
}
