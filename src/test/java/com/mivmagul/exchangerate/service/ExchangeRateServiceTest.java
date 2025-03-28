package com.mivmagul.exchangerate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {

    @Mock
    RestTemplate restTemplate;
    @InjectMocks
    ExchangeRateService service;

    @Test
    public void testGetExchangeRate() {
        // setup
        Map<String, Object> mockResponse = Map.of(
                "rates", Map.of("USD", 1.2, "EUR", 0.85)
        );
        when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(Map.class)))
                .thenReturn(mockResponse);
        // execute
        Number rate = service.getExchangeRate("GBP", "USD");
        // verify
        assertEquals(1.2, rate);
    }
}
