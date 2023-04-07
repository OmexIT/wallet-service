package com.logispin.wallet.repository;

import com.logispin.wallet.models.Wallet;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {}
