package com.logispin.wallet.mappers;

import com.logispin.wallet.dto.WalletDto;
import com.logispin.wallet.models.Wallet;
import org.mapstruct.Mapper;

@Mapper(uses = {TransactionMapper.class})
public interface WalletMapper {
  WalletDto toWalletDto(Wallet wallet);
}
