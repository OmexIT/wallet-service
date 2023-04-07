package com.logispin.wallet.services.transaction;

import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.models.Wallet;
import com.logispin.wallet.wrappers.WalletTestWrapper;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class BonusStrategyTest {

  @Test
  void testExecute_validAmount() {
    WalletTestWrapper walletWrapper =
        WalletTestWrapper.buildValid()
            .balance(BigDecimal.valueOf(50))
            .clearWalletTransactions()
            .build();
    Wallet wallet = walletWrapper.unwrapWallet();
    BonusStrategy strategy = new BonusStrategy();

    wallet = strategy.execute(wallet, BigDecimal.valueOf(20));

    Assertions.assertEquals(BigDecimal.valueOf(70), wallet.getBalance());
    Assertions.assertEquals(1, wallet.getWalletTransactions().size());
    Assertions.assertEquals(
        TransactionType.BONUS, wallet.getWalletTransactions().get(0).getTransactionType());
  }

  @Test
  void testExecute_invalidAmount() {
    WalletTestWrapper walletWrapper =
        WalletTestWrapper.buildValid()
            .balance(BigDecimal.valueOf(50))
            .clearWalletTransactions()
            .build();
    Wallet wallet = walletWrapper.unwrapWallet();
    BonusStrategy strategy = new BonusStrategy();
    var transactionAmount = BigDecimal.valueOf(-20);
    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class, () -> strategy.execute(wallet, transactionAmount));

    log.error("Error message: {}", exception.getMessage());
    Assertions.assertEquals("Invalid deposit amount", exception.getMessage());
    Assertions.assertEquals(BigDecimal.valueOf(50), wallet.getBalance());
    Assertions.assertEquals(0, wallet.getWalletTransactions().size());
  }

  @Test
  void testGetSupportedTransactionType() {
    BonusStrategy strategy = new BonusStrategy();
    Assertions.assertEquals(TransactionType.BONUS, strategy.getSupportedTransactionType());
  }
}
