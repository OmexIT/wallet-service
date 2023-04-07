package com.logispin.wallet.repository;

import com.logispin.wallet.models.WalletTransaction;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {
  @Query("SELECT wt FROM WalletTransaction wt WHERE wt.wallet.id = :walletId")
  List<WalletTransaction> findByWalletId(@Param("walletId") UUID walletId);
}
