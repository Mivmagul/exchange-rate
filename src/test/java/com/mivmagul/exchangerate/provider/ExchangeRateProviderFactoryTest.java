package com.mivmagul.exchangerate.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mivmagul.exchangerate.data.ExchangeRateProviderType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateProviderFactoryTest {

  @Mock private FixerExchangeRateProvider fixerProvider;

  @Mock private ExchangeRateHostProvider hostProvider;

  @InjectMocks private ExchangeRateProviderFactory factory;

  @Test
  public void testGetFixerProvider() {
    // execute
    ExchangeRateProvider provider = factory.getProvider(ExchangeRateProviderType.FIXER_IO);

    // verify
    assertEquals(fixerProvider, provider);
  }

  @Test
  public void testGetHostProvider() {
    // execute
    ExchangeRateProvider provider =
        factory.getProvider(ExchangeRateProviderType.EXCHANGE_RATE_HOST);

    // verify
    assertEquals(hostProvider, provider);
  }

  @Test
  public void testGetUnknownProvider() {
    // execute & verify
    assertThrows(IllegalArgumentException.class, () -> factory.getProvider(null));
  }
}
