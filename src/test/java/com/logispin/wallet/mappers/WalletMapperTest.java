package com.logispin.wallet.mappers;

import static org.junit.jupiter.api.Assertions.*;

import com.logispin.wallet.dto.WalletDto;
import com.logispin.wallet.models.Wallet;
import com.logispin.wallet.wrappers.WalletTestWrapper;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

class WalletMapperTest {

  private WalletMapper sut;

  @BeforeEach
  void setUp() {
    sut = Mappers.getMapper(WalletMapper.class);
  }

  @ParameterizedTest
  @MethodSource("provideWalleWrappers")
  void testToWalletDto_shouldMapAsExpected(WalletTestWrapper walletTestWrapper) {
    Wallet wallet = null;
    WalletDto expected = null;
    if (walletTestWrapper != null) {
      wallet = walletTestWrapper.unwrapWallet();
      expected = walletTestWrapper.unwrapWalletDto();
    }

    WalletDto actual = sut.toWalletDto(wallet);
    assertEquals(expected, actual);
    if (expected != null) {
      assertNotNull(actual);
      assertEquals(expected.customerId(), actual.customerId());
      assertEquals(expected.balance(), actual.balance());
      assertEquals(expected.id(), actual.id());
    }
  }

  public static Stream<Arguments> provideWalleWrappers() {
    return Stream.of(
        Arguments.of(WalletTestWrapper.buildValid().clearWalletTransactions().build()), null);
  }
}
