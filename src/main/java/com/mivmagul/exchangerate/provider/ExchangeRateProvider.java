package com.mivmagul.exchangerate.provider;

import java.util.Map;

public interface ExchangeRateProvider {
  Map<String, Object> fetchRates(String source);
}
