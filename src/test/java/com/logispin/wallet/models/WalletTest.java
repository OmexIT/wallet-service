package com.logispin.wallet.models;

import static org.junit.jupiter.api.Assertions.*;

import com.logispin.wallet.exceptions.InsufficientFundsException;
import com.logispin.wallet.wrappers.WalletTestWrapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class WalletTest {

  private Wallet wallet;

  @BeforeEach
  void setUp() {
    wallet = WalletTestWrapper.buildValid().balance(BigDecimal.ZERO).build().unwrapWallet();
    ReflectionTestUtils.setField(wallet, "balance", BigDecimal.ZERO);
  }

  @Test
  void deposit_positiveAmount_increasesBalance() {
    var initialBalance = (BigDecimal) ReflectionTestUtils.getField(wallet, "balance");
    var depositAmount = new BigDecimal("50");

    wallet.deposit(depositAmount);

    var newBalance = (BigDecimal) ReflectionTestUtils.getField(wallet, "balance");
    assertEquals(initialBalance.add(depositAmount), newBalance);
  }

  @Test
  void deposit_negativeAmount_throwsIllegalArgumentException() {
    var depositAmount = new BigDecimal("-50");

    assertThrows(IllegalArgumentException.class, () -> wallet.deposit(depositAmount));
  }

  @Test
  void withdraw_positiveAmount_decreasesBalance() {
    var initialBalance = (BigDecimal) ReflectionTestUtils.getField(wallet, "balance");
    var withdrawAmount = new BigDecimal("50");

    wallet.deposit(new BigDecimal("100"));
    wallet.withdraw(withdrawAmount);

    var newBalance = (BigDecimal) ReflectionTestUtils.getField(wallet, "balance");
    assertEquals(initialBalance.add(new BigDecimal("50")), newBalance);
  }

  @Test
  void withdraw_negativeAmount_throwsIllegalArgumentException() {
    var withdrawAmount = new BigDecimal("-50");

    assertThrows(IllegalArgumentException.class, () -> wallet.withdraw(withdrawAmount));
  }

  @Test
  void withdraw_moreThanBalance_throwsInsufficientFundsException() {
    var withdrawAmount = new BigDecimal("200");

    wallet.deposit(new BigDecimal("100"));

    assertThrows(InsufficientFundsException.class, () -> wallet.withdraw(withdrawAmount));
  }
}
