package com.logispin.wallet.services.transaction;

import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.models.Wallet;
import com.logispin.wallet.models.WalletTransaction;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class DepositStrategy implements TransactionStrategy {
  @Override
  public Wallet execute(Wallet wallet, BigDecimal amount) {
    wallet.deposit(amount);
    wallet.addTransaction(new WalletTransaction(wallet, TransactionType.DEPOSIT, amount));
    return wallet;
  }

  @Override
  public TransactionType getSupportedTransactionType() {
    return TransactionType.DEPOSIT;
  }
}
