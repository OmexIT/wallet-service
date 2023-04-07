package com.logispin.wallet.mappers;

import static org.junit.jupiter.api.Assertions.*;

import com.logispin.wallet.dto.TransactionDto;
import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.models.WalletTransaction;
import com.logispin.wallet.wrappers.WalletTransactionTestWrapper;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

class TransactionMapperTest {
  private TransactionMapper sut;

  @BeforeEach
  void setUp() {
    sut = Mappers.getMapper(TransactionMapper.class);
  }

  @ParameterizedTest
  @MethodSource("provideTransactionWrappers")
  void testToTransactionDto_ShouldMapCorrectly(
      WalletTransactionTestWrapper walletTransactionTestWrapper) {
    WalletTransaction walletTransaction = null;
    TransactionDto expected = null;
    if (walletTransactionTestWrapper != null) {
      walletTransaction = walletTransactionTestWrapper.unwrapWalletTransaction();
      expected = walletTransactionTestWrapper.unwrapTransactionDto();
    }

    TransactionDto actual = sut.toTransactionDto(walletTransaction);
    assertEquals(expected, actual);
    if (expected != null) {
      assertNotNull(actual);
      assertEquals(expected.type(), actual.type());
      assertEquals(expected.amount(), actual.amount());
      assertEquals(expected.id(), actual.id());
    }
  }

  @Test
  void testToTransactionDto_ShouldMapCorrectly() {
    WalletTransactionTestWrapper walletTransactionTestWrapper =
        WalletTransactionTestWrapper.buildValid().build();
    List<TransactionDto> expected = List.of(walletTransactionTestWrapper.unwrapTransactionDto());
    List<TransactionDto> actual =
        sut.toTransactionDto(List.of(walletTransactionTestWrapper.unwrapWalletTransaction()));

    assertEquals(expected, actual);
  }

  public static Stream<Arguments> provideTransactionWrappers() {
    return Stream.of(
        Arguments.of(WalletTransactionTestWrapper.buildValid().build()),
        Arguments.of(
            WalletTransactionTestWrapper.buildValid()
                .transactionType(TransactionType.WITHDRAWAL)
                .build()),
        Arguments.of(
            WalletTransactionTestWrapper.buildValid()
                .transactionType(TransactionType.DEPOSIT)
                .build()),
        Arguments.of(
            WalletTransactionTestWrapper.buildValid()
                .transactionType(TransactionType.BONUS)
                .build()),
        Arguments.of(
            WalletTransactionTestWrapper.buildValid()
                .transactionType(TransactionType.BET_WON)
                .build()),
        Arguments.of(
            WalletTransactionTestWrapper.buildValid()
                .transactionType(TransactionType.BET_PLACED)
                .build()),
        null);
  }
}
