package com.logispin.wallet.dto;

import com.logispin.wallet.models.TransactionType;
import java.math.BigDecimal;
import java.util.UUID;

public record TransactionDto(UUID id, TransactionType type, BigDecimal amount) {}
