package com.logispin.wallet.mappers;

import com.logispin.wallet.dto.TransactionDto;
import com.logispin.wallet.models.WalletTransaction;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TransactionMapper {

  @Mapping(target = "type", source = "transactionType")
  TransactionDto toTransactionDto(WalletTransaction walletTransaction);

  List<TransactionDto> toTransactionDto(List<WalletTransaction> walletTransactions);
}
