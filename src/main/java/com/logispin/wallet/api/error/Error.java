package com.logispin.wallet.api.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Error {

  @JsonProperty("Source")
  private final String source;

  @JsonProperty("ReasonCode")
  private final String reason;

  @JsonProperty("Description")
  private final String description;

  @JsonProperty("Recoverable")
  private final boolean recoverable;

  @JsonProperty("Details")
  private final String details;

  public Error(
      final String source, final String reason, final String description, final String details) {
    this(source, reason, description, details, false);
  }

  public Error(
      final String source,
      final String reason,
      final String description,
      final String details,
      final boolean recoverable) {
    this.source = source;
    this.reason = reason;
    this.description = description;
    this.details = details;
    this.recoverable = recoverable;
  }
}
