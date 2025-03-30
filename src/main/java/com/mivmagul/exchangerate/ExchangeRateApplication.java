package com.mivmagul.exchangerate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ExchangeRateApplication {
  public static void main(String[] args) {
    SpringApplication.run(ExchangeRateApplication.class, args);
  }
}
