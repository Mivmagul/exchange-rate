package com.mivmagul.exchangerate.provider;

import com.mivmagul.exchangerate.dto.CurrencyRate;
import java.util.Set;

public interface ExchangeRateProvider {
  Set<CurrencyRate> fetchRates(String baseCurrency);
}
