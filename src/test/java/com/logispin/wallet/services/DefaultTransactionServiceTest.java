package com.logispin.wallet.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.logispin.wallet.dto.TransactionDto;
import com.logispin.wallet.exceptions.ResourceNotFoundException;
import com.logispin.wallet.mappers.TransactionMapper;
import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.models.WalletTransaction;
import com.logispin.wallet.repository.WalletTransactionRepository;
import com.logispin.wallet.wrappers.WalletTestWrapper;
import com.logispin.wallet.wrappers.WalletTransactionTestWrapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultTransactionServiceTest {

  @InjectMocks private DefaultTransactionService transactionService;

  @Mock private WalletTransactionRepository walletTransactionRepository;

  @Mock private TransactionMapper transactionMapper;

  @Test
  void testGetTransactionsByWalletId() {
    UUID walletId = UUID.randomUUID();
    WalletTransactionTestWrapper walletTransactionTestWrapper1 =
        WalletTransactionTestWrapper.buildValid()
            .transactionType(TransactionType.DEPOSIT)
            .amount(BigDecimal.valueOf(100))
            .build();
    WalletTransactionTestWrapper walletTransactionTestWrapper2 =
        WalletTransactionTestWrapper.buildValid()
            .transactionType(TransactionType.WITHDRAWAL)
            .amount(BigDecimal.valueOf(50))
            .build();

    WalletTransaction walletTransaction1 = walletTransactionTestWrapper1.unwrapWalletTransaction();
    WalletTransaction walletTransaction2 = walletTransactionTestWrapper2.unwrapWalletTransaction();
    List<WalletTransaction> transactions = Arrays.asList(walletTransaction1, walletTransaction2);

    TransactionDto transactionDto1 = walletTransactionTestWrapper1.unwrapTransactionDto();
    TransactionDto transactionDto2 = walletTransactionTestWrapper2.unwrapTransactionDto();

    List<TransactionDto> transactionDtos = Arrays.asList(transactionDto1, transactionDto2);

    when(walletTransactionRepository.findByWalletId(walletId)).thenReturn(transactions);
    when(transactionMapper.toTransactionDto(transactions)).thenReturn(transactionDtos);

    List<TransactionDto> result = transactionService.getTransactionsByWalletId(walletId);

    assertEquals(2, result.size());
    assertTrue(result.contains(transactionDto1));
    assertTrue(result.contains(transactionDto2));

    verify(walletTransactionRepository, times(1)).findByWalletId(walletId);
    verify(transactionMapper, times(1)).toTransactionDto(transactions);
    verifyNoMoreInteractions(walletTransactionRepository, transactionMapper);
  }

  @Test
  void testGetTransactionById() {
    UUID transactionId = UUID.randomUUID();
    UUID walletId = UUID.randomUUID();
    WalletTransactionTestWrapper walletTransactionTestWrapper =
        WalletTransactionTestWrapper.buildValid()
            .id(transactionId)
            .wallet(WalletTestWrapper.buildValid().id(walletId).build().unwrapWallet())
            .transactionType(TransactionType.DEPOSIT)
            .amount(BigDecimal.valueOf(100))
            .build();

    WalletTransaction walletTransaction = walletTransactionTestWrapper.unwrapWalletTransaction();
    TransactionDto transactionDto = walletTransactionTestWrapper.unwrapTransactionDto();

    when(walletTransactionRepository.findById(transactionId))
        .thenReturn(Optional.of(walletTransaction));
    when(transactionMapper.toTransactionDto(walletTransaction)).thenReturn(transactionDto);

    TransactionDto result = transactionService.getTransactionById(transactionId);

    assertThat(result).isNotNull().isEqualTo(transactionDto);
    verify(walletTransactionRepository, times(1)).findById(transactionId);
    verify(transactionMapper, times(1)).toTransactionDto(walletTransaction);
  }

  @Test
  void testGetTransactionById_notFound() {
    UUID transactionId = UUID.randomUUID();

    when(walletTransactionRepository.findById(transactionId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> transactionService.getTransactionById(transactionId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Wallet transaction with id");

    verify(walletTransactionRepository, times(1)).findById(transactionId);
    verify(transactionMapper, never()).toTransactionDto(any(WalletTransaction.class));
  }
}
