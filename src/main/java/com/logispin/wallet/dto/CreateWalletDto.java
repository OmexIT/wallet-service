package com.logispin.wallet.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateWalletDto(@NotNull UUID customerId) {}
