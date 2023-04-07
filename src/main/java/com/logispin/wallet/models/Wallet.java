package com.logispin.wallet.models;

import com.logispin.wallet.exceptions.InsufficientFundsException;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "wallet")
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class Wallet extends Auditable<String> implements Serializable {

  @Id
  @Column(name = "id", nullable = false)
  private final UUID id;

  @Column(nullable = false)
  private BigDecimal balance;

  @Version private Integer version;

  @Column(nullable = false, unique = true)
  private final UUID customerId;

  @OneToMany(
      mappedBy = "wallet",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private final List<WalletTransaction> walletTransactions = new ArrayList<>();

  public Wallet(UUID customerId) {
    this.id = UUID.randomUUID();
    this.customerId = customerId;
    this.balance = BigDecimal.ZERO;
    this.version = 0;
  }

  public void deposit(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Invalid deposit amount");
    }
    this.balance = this.balance.add(amount);
  }

  public void withdraw(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Invalid withdraw amount");
    }
    var newBalance = this.balance.subtract(amount);
    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new InsufficientFundsException("Insufficient funds in wallet");
    }
    this.balance = newBalance;
  }

  public List<WalletTransaction> getWalletTransactions() {
    return Collections.unmodifiableList(this.walletTransactions);
  }

  public void addTransaction(WalletTransaction walletTransaction) {
    walletTransactions.add(walletTransaction);
  }
}
