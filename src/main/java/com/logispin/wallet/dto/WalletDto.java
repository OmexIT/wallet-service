package com.logispin.wallet.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletDto(UUID id, BigDecimal balance, UUID customerId) {}
