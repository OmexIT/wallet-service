package com.logispin.wallet.api.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class ErrorBlock {

  @JsonProperty("Error")
  private final List<com.logispin.wallet.api.error.Error> errors = new ArrayList<>();

  public ErrorBlock(final String source, final ErrorReason reason, final List<String> details) {
    for (String detail : details) {
      this.errors.add(new Error(source, reason.getReason(), reason.getDescription(), detail));
    }
  }

  public ErrorBlock(
      final String source,
      final ErrorReason reason,
      final String details,
      final boolean recoverable) {
    this(source, reason.getReason(), reason.getDescription(), details, recoverable);
  }

  public ErrorBlock(
      final String source,
      final String reason,
      final String description,
      final String details,
      final boolean recoverable) {
    this.errors.add(new Error(source, reason, description, details, recoverable));
  }

  public static ErrorBlock fromDefaultAttributeMap(
      final Map<String, Object> defaultErrorAttributes) {
    // original attribute values are documented in
    // org.springframework.boot.web.servlet.error.DefaultErrorAttributes
    return new ErrorBlock(
        (String) defaultErrorAttributes.getOrDefault("path", "no source available"),
        ((Integer) defaultErrorAttributes.get("status")).toString(),
        (String) defaultErrorAttributes.getOrDefault("error", "no description available"),
        (String) defaultErrorAttributes.getOrDefault("message", "no detail available"),
        (Boolean) defaultErrorAttributes.getOrDefault("recoverable", Boolean.FALSE));
  }

  // utility method to return a map of serialized root attributes,
  // see the last part of the guide for more details
  public Map<String, Object> toAttributeMap() {
    return Map.of("errors", errors);
  }
}
