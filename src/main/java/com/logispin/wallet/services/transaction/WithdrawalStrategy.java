package com.logispin.wallet.services.transaction;

import com.logispin.wallet.exceptions.InsufficientFundsException;
import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.models.Wallet;
import com.logispin.wallet.models.WalletTransaction;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class WithdrawalStrategy implements TransactionStrategy {
  @Override
  public Wallet execute(Wallet wallet, BigDecimal amount) {
    if (wallet.getBalance().compareTo(amount) < 0) {
      throw new InsufficientFundsException("Insufficient funds in wallet");
    }

    wallet.withdraw(amount);
    wallet.addTransaction(new WalletTransaction(wallet, TransactionType.WITHDRAWAL, amount));
    return wallet;
  }

  @Override
  public TransactionType getSupportedTransactionType() {
    return TransactionType.WITHDRAWAL;
  }
}
