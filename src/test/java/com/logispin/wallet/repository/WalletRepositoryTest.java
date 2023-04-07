package com.logispin.wallet.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.logispin.wallet.config.DataSourceConfig;
import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.models.Wallet;
import com.logispin.wallet.models.WalletTransaction;
import com.logispin.wallet.wrappers.WalletTestWrapper;
import com.logispin.wallet.wrappers.WalletTransactionTestWrapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

@Import(DataSourceConfig.class)
@DataJpaTest
class WalletRepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private WalletRepository walletRepository;

  @Test
  @WithMockUser(username = "testUser")
  void saveAndFindWallet() {
    UUID customerId = UUID.fromString("41c0c231-b945-4341-8276-8fc7a3b9e9d0");
    Wallet wallet =
        WalletTestWrapper.buildValid()
            .customerId(customerId)
            .clearWalletTransactions()
            .build()
            .unwrapWallet();
    var transactionId = UUID.fromString("7137d433-7ce8-40c9-abad-1d687ad06d03");
    WalletTransaction walletTransaction =
        WalletTransactionTestWrapper.buildValid()
            .wallet(wallet)
            .amount(BigDecimal.TEN)
            .id(transactionId)
            .transactionType(TransactionType.DEPOSIT)
            .build()
            .unwrapWalletTransaction();
    wallet.addTransaction(walletTransaction);

    Wallet savedWallet = entityManager.persistAndFlush(wallet);

    Wallet foundWallet = walletRepository.findById(savedWallet.getId()).orElse(null);
    assertThat(wallet.getId()).isNotNull();

    assertThat(foundWallet).isNotNull();
    assertThat(foundWallet.getCustomerId()).isEqualTo(customerId);
    assertThat(foundWallet.getCreatedBy()).isNotNull();
    assertThat(foundWallet.getCreatedOn()).isNotNull();
    assertThat(foundWallet.getLastModifiedOn()).isNotNull();
    assertThat(foundWallet.getLastModifiedBy()).isNotNull();
    assertThat(foundWallet.getVersion()).isZero();

    List<WalletTransaction> walletTransactions = wallet.getWalletTransactions();
    assertThat(walletTransactions).isNotEmpty();
    WalletTransaction walletTransaction1 = walletTransactions.get(0);
    assertThat(walletTransaction1.getId()).isEqualTo(transactionId);
    assertThat(walletTransaction1.getTransactionType()).isEqualTo(TransactionType.DEPOSIT);
    assertThat(walletTransaction1.getAmount()).isEqualTo(BigDecimal.TEN);
    assertThat(walletTransaction1.getWallet()).isNotNull();
  }
}
