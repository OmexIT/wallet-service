package com.logispin.wallet.services.transaction;

import com.logispin.wallet.models.TransactionType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WalletStrategiesRegistry {
  private final Map<TransactionType, TransactionStrategy> executors = new HashMap<>();

  public WalletStrategiesRegistry(final List<TransactionStrategy> executors) {
    this.executors.putAll(
        executors.stream()
            .collect(
                Collectors.toMap(
                    TransactionStrategy::getSupportedTransactionType, Function.identity())));
  }

  public TransactionStrategy resolve(final TransactionType type) {
    log.trace("Resolving TransactionStrategy of type: [{}]", type);

    return executors.computeIfAbsent(
        type,
        t -> {
          throw new IllegalStateException(String.format("Strategy type [%s] not resolved.", t));
        });
  }
}
