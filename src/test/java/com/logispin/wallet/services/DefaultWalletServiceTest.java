package com.logispin.wallet.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.logispin.wallet.config.AppConfig;
import com.logispin.wallet.dto.CreateWalletDto;
import com.logispin.wallet.dto.TransactionDto;
import com.logispin.wallet.dto.WalletDto;
import com.logispin.wallet.exceptions.ResourceNotFoundException;
import com.logispin.wallet.mappers.TransactionMapperImpl;
import com.logispin.wallet.mappers.WalletMapper;
import com.logispin.wallet.mappers.WalletMapperImpl;
import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.models.Wallet;
import com.logispin.wallet.repository.WalletRepository;
import com.logispin.wallet.services.transaction.*;
import com.logispin.wallet.wrappers.WalletTestWrapper;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DirtiesContext
@ExtendWith({MockitoExtension.class, SpringExtension.class})
@SpringBootTest(
    classes = {
      DefaultWalletServiceTest.TestTransactionConfiguration.class,
      BetPlacedStrategy.class,
      BetWonStrategy.class,
      BonusStrategy.class,
      DepositStrategy.class,
      WithdrawalStrategy.class,
      WalletMapperImpl.class,
      TransactionMapperImpl.class
    })
class DefaultWalletServiceTest {

  @MockBean private WalletRepository walletRepository;
  @Autowired private DefaultWalletService walletService;
  @Autowired private WalletMapper walletMapper;

  @ParameterizedTest
  @MethodSource("provideTransactionParameters")
  void testProcessTransaction(
      TransactionType transactionType,
      BigDecimal walletBalance,
      BigDecimal transactionAmount,
      BigDecimal expectedWalletBalance) {
    var walletId = UUID.randomUUID();
    Wallet wallet =
        WalletTestWrapper.buildValid()
            .balance(walletBalance)
            .clearWalletTransactions()
            .build()
            .unwrapWallet();

    when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
    when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

    WalletDto updatedWallet =
        walletService.processTransaction(walletId, transactionType, transactionAmount);
    verify(walletRepository).findById(walletId);
    verify(walletRepository).save(any(Wallet.class));
    assertEquals(expectedWalletBalance, updatedWallet.balance());
  }

  @Test
  void testProcessTransaction_walletNotFound() {
    var walletId = UUID.randomUUID();

    when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () ->
                walletService.processTransaction(
                    walletId, TransactionType.DEPOSIT, BigDecimal.TEN));
    assertEquals("Wallet not found: " + walletId, exception.getMessage());
    verify(walletRepository).findById(walletId);
    verify(walletRepository, times(0)).save(any(Wallet.class));
  }

  @Test
  void testHandleOptimisticLockingFailure() {
    var walletId = UUID.randomUUID();
    Wallet wallet =
        WalletTestWrapper.buildValid()
            .balance(BigDecimal.ZERO)
            .clearWalletTransactions()
            .build()
            .unwrapWallet();
    TransactionDto transaction =
        new TransactionDto(walletId, TransactionType.DEPOSIT, BigDecimal.TEN);

    when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

    var saveAttempts = new AtomicInteger(0);

    doAnswer(
            invocation -> {
              if (saveAttempts.incrementAndGet() < 4) {
                throw new ObjectOptimisticLockingFailureException(Wallet.class, walletId);
              } else {
                Wallet savedWallet = invocation.getArgument(0);
                savedWallet.deposit(transaction.amount());
                return savedWallet;
              }
            })
        .when(walletRepository)
        .save(any(Wallet.class));

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () ->
                walletService.processTransaction(
                    walletId, TransactionType.DEPOSIT, BigDecimal.TEN));

    assertEquals("Unable to update wallet balance after several retries", exception.getMessage());

    verify(walletRepository, times(3)).findById(walletId);
    verify(walletRepository, times(3)).save(any(Wallet.class));
  }

  @Test
  void testCreateWallet() {

    UUID walletId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();
    WalletTestWrapper walletTestWrapper =
        WalletTestWrapper.buildValid().id(walletId).customerId(customerId).build();
    Wallet wallet = walletTestWrapper.unwrapWallet();
    WalletDto expected = walletTestWrapper.unwrapWalletDto();
    CreateWalletDto request = new CreateWalletDto(customerId);

    when(walletRepository.save(any())).thenReturn(wallet);

    WalletDto actual = walletService.createWallet(request);
    assertAll(
        "createWallet",
        () -> assertEquals(expected, actual),
        () -> verify(walletRepository, times(1)).save(any()));
  }

  @Test
  void testGetWalletById() {
    UUID walletId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();
    WalletTestWrapper walletTestWrapper =
        WalletTestWrapper.buildValid().id(walletId).customerId(customerId).build();
    Wallet wallet = walletTestWrapper.unwrapWallet();
    WalletDto expected = walletTestWrapper.unwrapWalletDto();

    when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

    WalletDto actual = walletService.getWalletById(walletId);

    assertAll(
        "getWalletById",
        () -> assertEquals(expected, actual),
        () -> verify(walletRepository).findById(walletId));
  }

  @Test
  void testGetWalletByIdNotFound() {
    UUID walletId = UUID.randomUUID();

    when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

    Throwable exception =
        assertThrows(ResourceNotFoundException.class, () -> walletService.getWalletById(walletId));

    assertEquals("Wallet not found: " + walletId, exception.getMessage());
  }

  public static Stream<Arguments> provideTransactionParameters() {
    return Stream.of(
        Arguments.of(TransactionType.DEPOSIT, BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.TEN),
        Arguments.of(
            TransactionType.WITHDRAWAL,
            BigDecimal.valueOf(21),
            BigDecimal.TEN,
            BigDecimal.valueOf(11)),
        Arguments.of(
            TransactionType.BET_WON, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.valueOf(20)),
        Arguments.of(
            TransactionType.BET_PLACED,
            BigDecimal.valueOf(100),
            BigDecimal.TEN,
            BigDecimal.valueOf(90)),
        Arguments.of(
            TransactionType.BONUS,
            BigDecimal.TEN,
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(110)));
  }

  @Import({AppConfig.class})
  @ComponentScan(
      useDefaultFilters = false,
      includeFilters =
          @ComponentScan.Filter(
              type = FilterType.ASSIGNABLE_TYPE,
              value = {DefaultWalletService.class}))
  public static class TestTransactionConfiguration {}
}
