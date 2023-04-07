package com.logispin.wallet.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "wallet_transactions")
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class WalletTransaction extends Auditable<String> implements Serializable {

  @Id private UUID id;

  @ManyToOne
  @JoinColumn(name = "wallet_id", nullable = false)
  private Wallet wallet;

  @Enumerated(EnumType.STRING)
  private TransactionType transactionType;

  private BigDecimal amount;

  public WalletTransaction(Wallet wallet, TransactionType transactionType, BigDecimal amount) {
    this.id = UUID.randomUUID();
    this.wallet = wallet;
    this.transactionType = transactionType;
    this.amount = amount;
  }
}
