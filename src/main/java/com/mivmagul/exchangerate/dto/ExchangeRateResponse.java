package com.mivmagul.exchangerate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class ExchangeRateResponse {
  @JsonProperty("base")
  private String baseCurrency;

  @JsonProperty("rates")
  private Map<String, BigDecimal> rates;
}
