package com.logispin.wallet.services.transaction;

import com.logispin.wallet.models.TransactionType;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WalletStrategiesRegistryTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(WalletStrategiesRegistryTest.class);

  private WalletStrategiesRegistry registry;

  @BeforeEach
  void setUp() {
    List<TransactionStrategy> strategies =
        Arrays.asList(
            new BetPlacedStrategy(),
            new BetWonStrategy(),
            new BonusStrategy(),
            new DepositStrategy(),
            new WithdrawalStrategy());
    registry = new WalletStrategiesRegistry(strategies);
  }

  @Test
  void testResolve_validStrategy() {
    TransactionStrategy depositStrategy = registry.resolve(TransactionType.DEPOSIT);
    Assertions.assertNotNull(depositStrategy);
    Assertions.assertEquals(TransactionType.DEPOSIT, depositStrategy.getSupportedTransactionType());
  }

  @Test
  void testResolve_invalidStrategyNotValidEnumValue() {
    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> registry.resolve(TransactionType.valueOf("INVALID_TYPE")));

    LOGGER.error("Error message: {}", exception.getMessage());
    Assertions.assertEquals(
        "No enum constant com.logispin.wallet.models.TransactionType.INVALID_TYPE",
        exception.getMessage());
  }

  @Test
  void testResolve_validStrategyNotRegistered() throws Exception {
    List<TransactionStrategy> strategiesWithUnsupported =
        Arrays.asList(
            new BetPlacedStrategy(),
            new BetWonStrategy(),
            new BonusStrategy(),
            new DepositStrategy());
    registry = new WalletStrategiesRegistry(strategiesWithUnsupported);

    IllegalStateException exception =
        Assertions.assertThrows(
            IllegalStateException.class, () -> registry.resolve(TransactionType.WITHDRAWAL));

    LOGGER.error("Error message: {}", exception.getMessage());
    Assertions.assertEquals("Strategy type [WITHDRAWAL] not resolved.", exception.getMessage());
  }
}
