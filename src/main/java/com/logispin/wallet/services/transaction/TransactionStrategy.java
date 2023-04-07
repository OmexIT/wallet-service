package com.logispin.wallet.services.transaction;

import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.models.Wallet;
import java.math.BigDecimal;

public interface TransactionStrategy {
  Wallet execute(Wallet wallet, BigDecimal amount);

  TransactionType getSupportedTransactionType();
}
