package com.logispin.wallet.services.transaction;

import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.models.Wallet;
import com.logispin.wallet.wrappers.WalletTestWrapper;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class DepositStrategyTest {

  @Test
  void testExecute_validAmount() {
    WalletTestWrapper walletWrapper =
        WalletTestWrapper.buildValid()
            .balance(BigDecimal.valueOf(50))
            .clearWalletTransactions()
            .build();
    Wallet wallet = walletWrapper.unwrapWallet();
    DepositStrategy strategy = new DepositStrategy();

    wallet = strategy.execute(wallet, BigDecimal.valueOf(30));

    Assertions.assertEquals(BigDecimal.valueOf(80), wallet.getBalance());
    Assertions.assertEquals(1, wallet.getWalletTransactions().size());
    Assertions.assertEquals(
        TransactionType.DEPOSIT, wallet.getWalletTransactions().get(0).getTransactionType());
  }

  @Test
  void testExecute_invalidAmount() {
    WalletTestWrapper walletWrapper =
        WalletTestWrapper.buildValid()
            .balance(BigDecimal.valueOf(50))
            .clearWalletTransactions()
            .build();
    Wallet wallet = walletWrapper.unwrapWallet();
    DepositStrategy strategy = new DepositStrategy();

    var transactionAmount = BigDecimal.valueOf(-30);
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
    DepositStrategy strategy = new DepositStrategy();
    Assertions.assertEquals(TransactionType.DEPOSIT, strategy.getSupportedTransactionType());
  }
}
