package com.logispin.wallet.services;

import com.logispin.wallet.dto.CreateWalletDto;
import com.logispin.wallet.dto.WalletDto;
import com.logispin.wallet.exceptions.ResourceNotFoundException;
import com.logispin.wallet.models.TransactionType;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

public interface WalletService {

  @Retryable(
      retryFor = {ObjectOptimisticLockingFailureException.class},
      notRecoverable = {ResourceNotFoundException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 50))
  WalletDto processTransaction(UUID walletId, TransactionType type, BigDecimal amount);

  @Recover
  WalletDto handleOptimisticLockingFailure(
      ObjectOptimisticLockingFailureException exception,
      UUID walletId,
      TransactionType type,
      BigDecimal amount);

  WalletDto createWallet(CreateWalletDto request);

  WalletDto getWalletById(UUID walletId);
}
