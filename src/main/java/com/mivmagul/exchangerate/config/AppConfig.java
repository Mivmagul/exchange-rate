package com.mivmagul.exchangerate.config;

import com.mivmagul.exchangerate.data.ExchangeRateProviderType;
import com.mivmagul.exchangerate.provider.ExchangeRateProvider;
import com.mivmagul.exchangerate.provider.FixerExchangeRateProvider;
import com.mivmagul.exchangerate.provider.HostExchangeRateProvider;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

  @Value("${exchange-rate.fixer.api.endpoint}")
  private String fixerEndpoint;

  @Value("${exchange-rate.fixer.api.access-key}")
  private String fixerAccessKey;

  @Value("${exchange-rate.host.api.endpoint}")
  private String hostEndpoint;

  @Value("${exchange-rate.host.api.access-key}")
  private String hostAccessKey;

  @Value("${exchange-rate.provider-priorities}")
  private List<ExchangeRateProviderType> providerPriorities;

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public ExchangeRateProvider fixerExchangeRateProvider(RestTemplate restTemplate) {
    return new FixerExchangeRateProvider(restTemplate, fixerEndpoint, fixerAccessKey);
  }

  @Bean
  public ExchangeRateProvider hostExchangeRateProvider(RestTemplate restTemplate) {
    return new HostExchangeRateProvider(restTemplate, hostEndpoint, hostAccessKey);
  }

  @Bean
  public List<ExchangeRateProvider> exchangeRateProviders(
      ExchangeRateProvider fixerExchangeRateProvider,
      ExchangeRateProvider hostExchangeRateProvider) {
    return providerPriorities.stream()
        .map(
            priority ->
                switch (priority) {
                  case FIXER_IO -> fixerExchangeRateProvider;
                  case EXCHANGE_RATE_HOST -> hostExchangeRateProvider;
                })
        .collect(Collectors.toList());
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .addServersItem(new Server().url("/"))
        .info(apiInfo())
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(
            new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("bearerAuth", securityScheme()));
  }

  private Info apiInfo() {
    return new Info()
        .title("Exchange Rate API")
        .version("1.0")
        .description("API for working with exchange rates")
        .contact(new Contact().name("Vadym").email("mivmagul@gmail.com"));
  }

  private SecurityScheme securityScheme() {
    return new SecurityScheme()
        .name("Authorization")
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT");
  }
}
