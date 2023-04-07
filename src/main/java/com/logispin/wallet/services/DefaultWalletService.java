package com.logispin.wallet.services;

import com.logispin.wallet.dto.CreateWalletDto;
import com.logispin.wallet.dto.WalletDto;
import com.logispin.wallet.exceptions.ResourceNotFoundException;
import com.logispin.wallet.mappers.WalletMapper;
import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.models.Wallet;
import com.logispin.wallet.repository.WalletRepository;
import com.logispin.wallet.services.transaction.TransactionStrategy;
import com.logispin.wallet.services.transaction.WalletStrategiesRegistry;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultWalletService implements WalletService {

  private final WalletStrategiesRegistry walletStrategiesRegistry;
  private final WalletRepository walletRepository;
  private final WalletMapper walletMapper;

  public DefaultWalletService(
      final List<TransactionStrategy> transactionStrategies,
      WalletRepository walletRepository,
      WalletMapper walletMapper) {
    this.walletStrategiesRegistry = new WalletStrategiesRegistry(transactionStrategies);
    this.walletRepository = walletRepository;
    this.walletMapper = walletMapper;
  }

  @Transactional
  public WalletDto processTransaction(UUID walletId, TransactionType type, BigDecimal amount) {
    Wallet wallet =
        walletRepository
            .findById(walletId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + walletId));
    TransactionStrategy strategy = walletStrategiesRegistry.resolve(type);
    Wallet updatedWallet = strategy.execute(wallet, amount);
    return walletMapper.toWalletDto(walletRepository.save(updatedWallet));
  }

  public WalletDto handleOptimisticLockingFailure(
      ObjectOptimisticLockingFailureException exception,
      UUID walletId,
      TransactionType type,
      BigDecimal amount) {
    throw new IllegalStateException("Unable to update wallet balance after several retries");
  }

  @Override
  public WalletDto createWallet(CreateWalletDto request) {
    return walletMapper.toWalletDto(walletRepository.save(new Wallet(request.customerId())));
  }

  @Override
  public WalletDto getWalletById(UUID walletId) {
    Wallet wallet =
        walletRepository
            .findById(walletId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + walletId));
    return walletMapper.toWalletDto(wallet);
  }
}
