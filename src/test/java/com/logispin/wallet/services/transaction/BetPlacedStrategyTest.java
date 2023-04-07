package com.logispin.wallet.services.transaction;

import com.logispin.wallet.exceptions.InsufficientFundsException;
import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.models.Wallet;
import com.logispin.wallet.wrappers.WalletTestWrapper;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
class BetPlacedStrategyTest {

  @Test
  void testExecute_withSufficientFunds() {
    WalletTestWrapper walletWrapper =
        WalletTestWrapper.buildValid()
            .balance(BigDecimal.valueOf(100))
            .clearWalletTransactions()
            .build();
    Wallet wallet = walletWrapper.unwrapWallet();
    BetPlacedStrategy strategy = new BetPlacedStrategy();

    wallet = strategy.execute(wallet, BigDecimal.valueOf(50));

    Assertions.assertEquals(BigDecimal.valueOf(50), wallet.getBalance());
    Assertions.assertEquals(1, wallet.getWalletTransactions().size());
    Assertions.assertEquals(
        TransactionType.BET_PLACED, wallet.getWalletTransactions().get(0).getTransactionType());
  }

  @ParameterizedTest
  @ValueSource(floats = {0, 50, 99.9f})
  void testExecute_withInsufficientFunds(float balance) {
    WalletTestWrapper walletWrapper =
        WalletTestWrapper.buildValid()
            .balance(BigDecimal.valueOf(balance))
            .clearWalletTransactions()
            .build();
    Wallet wallet = walletWrapper.unwrapWallet();
    BetPlacedStrategy strategy = new BetPlacedStrategy();
    var amount = BigDecimal.valueOf(100);
    InsufficientFundsException exception =
        Assertions.assertThrows(
            InsufficientFundsException.class, () -> strategy.execute(wallet, amount));

    log.error("Error message: {}", exception.getMessage());
    Assertions.assertEquals("Insufficient funds in wallet", exception.getMessage());
    Assertions.assertEquals(BigDecimal.valueOf(balance), wallet.getBalance());
    Assertions.assertEquals(0, wallet.getWalletTransactions().size());
  }

  @Test
  void testGetSupportedTransactionType() {
    BetPlacedStrategy strategy = new BetPlacedStrategy();
    Assertions.assertEquals(TransactionType.BET_PLACED, strategy.getSupportedTransactionType());
  }
}
