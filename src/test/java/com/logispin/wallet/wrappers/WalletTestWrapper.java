package com.logispin.wallet.wrappers;

import com.logispin.wallet.dto.TransactionDto;
import com.logispin.wallet.dto.WalletDto;
import com.logispin.wallet.models.Wallet;
import com.logispin.wallet.models.WalletTransaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.springframework.test.util.ReflectionTestUtils;

@Data
@Builder(toBuilder = true)
public class WalletTestWrapper {
  private UUID id;
  private BigDecimal balance;
  private Integer version;
  private UUID customerId;

  @Singular
  private List<WalletTransactionTestWrapper.WalletTransactionTestWrapperBuilder> walletTransactions;

  public static WalletTestWrapperBuilder buildValid() {
    return builder()
        .id(UUID.randomUUID())
        .balance(BigDecimal.ZERO)
        .version(0)
        .customerId(UUID.randomUUID())
        .walletTransaction(WalletTransactionTestWrapper.buildValid());
  }

  public Wallet unwrapWallet() {
    Wallet wallet = new Wallet(this.customerId);

    ReflectionTestUtils.setField(wallet, "id", this.id);
    ReflectionTestUtils.setField(wallet, "balance", this.balance);
    ReflectionTestUtils.setField(wallet, "customerId", this.customerId);
    ReflectionTestUtils.setField(wallet, "version", this.version);

    if (this.walletTransactions != null) {
      List<WalletTransaction> unwrappedWalletTransactions =
          this.walletTransactions.stream()
              .map(
                  walletTransactionTestWrapperBuilder ->
                      walletTransactionTestWrapperBuilder.build().unwrapWalletTransaction())
              .collect(Collectors.toList());
      ReflectionTestUtils.setField(wallet, "walletTransactions", unwrappedWalletTransactions);
    }
    return wallet;
  }

  public WalletDto unwrapWalletDto() {
    List<TransactionDto> transactionDtos = null;

    return new WalletDto(this.id, this.balance, this.customerId);
  }
}
