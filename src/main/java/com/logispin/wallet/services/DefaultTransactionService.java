package com.logispin.wallet.services;

import com.logispin.wallet.dto.TransactionDto;
import com.logispin.wallet.exceptions.ResourceNotFoundException;
import com.logispin.wallet.mappers.TransactionMapper;
import com.logispin.wallet.repository.WalletTransactionRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultTransactionService implements TransactionService {
  private final WalletTransactionRepository walletTransactionRepository;

  private final TransactionMapper transactionMapper;

  @Override
  public List<TransactionDto> getTransactionsByWalletId(UUID walletId) {
    return transactionMapper.toTransactionDto(walletTransactionRepository.findByWalletId(walletId));
  }

  public TransactionDto getTransactionById(UUID transactionId) {
    return transactionMapper.toTransactionDto(
        walletTransactionRepository
            .findById(transactionId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        String.format(
                            "Wallet transaction with id [%s] not found", transactionId))));
  }
}
