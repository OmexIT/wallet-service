package com.logispin.wallet.services.transaction;

import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.models.Wallet;
import com.logispin.wallet.models.WalletTransaction;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BetWonStrategy implements TransactionStrategy {
  @Override
  public Wallet execute(Wallet wallet, BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Invalid deposit amount");
    }

    wallet.deposit(amount);
    wallet.addTransaction(new WalletTransaction(wallet, TransactionType.BET_WON, amount));
    return wallet;
  }

  @Override
  public TransactionType getSupportedTransactionType() {
    return TransactionType.BET_WON;
  }
}
