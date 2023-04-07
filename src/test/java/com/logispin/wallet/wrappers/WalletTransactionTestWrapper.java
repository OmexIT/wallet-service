package com.logispin.wallet.wrappers;

import com.logispin.wallet.dto.TransactionDto;
import com.logispin.wallet.models.TransactionType;
import com.logispin.wallet.models.Wallet;
import com.logispin.wallet.models.WalletTransaction;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import org.springframework.test.util.ReflectionTestUtils;

@Data
@Builder(toBuilder = true)
public class WalletTransactionTestWrapper {
  private UUID id;
  private Wallet wallet;
  private TransactionType transactionType;
  private BigDecimal amount;

  public static WalletTransactionTestWrapperBuilder buildValid() {
    return builder()
        .id(UUID.randomUUID())
        .transactionType(TransactionType.DEPOSIT)
        .amount(BigDecimal.TEN);
  }

  public WalletTransaction unwrapWalletTransaction() {
    WalletTransaction walletTransaction =
        new WalletTransaction(this.wallet, this.transactionType, this.amount);
    ReflectionTestUtils.setField(walletTransaction, "id", this.id);
    return walletTransaction;
  }

  public TransactionDto unwrapTransactionDto() {
    return new TransactionDto(this.id, this.transactionType, this.amount);
  }
}
