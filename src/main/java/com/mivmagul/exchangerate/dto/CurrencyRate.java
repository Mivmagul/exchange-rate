package com.mivmagul.exchangerate.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRate implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String currencyCode;
  private BigDecimal rate;
}
