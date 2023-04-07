package com.logispin.wallet.services.transaction;

import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.models.Wallet;
import com.logispin.wallet.models.WalletTransaction;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BonusStrategy implements TransactionStrategy {
  @Override
  public Wallet execute(Wallet wallet, BigDecimal amount) {
    wallet.deposit(amount);
    wallet.addTransaction(new WalletTransaction(wallet, TransactionType.BONUS, amount));
    return wallet;
  }

  @Override
  public TransactionType getSupportedTransactionType() {
    return TransactionType.BONUS;
  }
}
