package com.logispin.wallet.services;

import com.logispin.wallet.dto.TransactionDto;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

  List<TransactionDto> getTransactionsByWalletId(UUID walletId);
  ;

  TransactionDto getTransactionById(UUID transactionId);
}
