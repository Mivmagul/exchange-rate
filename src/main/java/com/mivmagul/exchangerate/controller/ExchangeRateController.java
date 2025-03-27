package com.mivmagul.exchangerate.controller;

import com.mivmagul.exchangerate.service.ExchangeRateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/rate/{from}/{to}")
    public Number getExchangeRate(@PathVariable String from, @PathVariable String to) {
        return exchangeRateService.getExchangeRate(from, to);
    }

    @GetMapping("/rates/{from}")
    public Map<String, Number> getAllExchangeRates(@PathVariable String from) {
        return exchangeRateService.getAllExchangeRates(from);
    }

    @GetMapping("/convert/{from}/{to}")
    public Number convertValue(@PathVariable String from, @PathVariable String to, @RequestParam Double amount) {
        return exchangeRateService.convertValue(from, to, amount);
    }

    @GetMapping("/convert/{from}")
    public Map<String, Double> convertToMultipleCurrencies(@PathVariable String from, @RequestParam Double amount, @RequestParam List<String> currencies) {
        return exchangeRateService.convertToMultipleCurrencies(from, amount, currencies);
    }
}
