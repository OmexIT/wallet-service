package com.logispin.wallet.api.error;

public enum ErrorReason {
  BAD_REQUEST("BAD_REQUEST", "Invalid Request Parameters"),
  NOT_FOUND("NOT_FOUND", "Resource Not Found"),
  INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Oh Snap! System Error!"),
  UNAUTHORIZED("UNAUTHORIZED", "Unauthorized Access"),
  NOT_IMPLEMENTED("NOT_IMPLEMENTED", "Requested action not implemented");

  private String reason;
  private String description;

  ErrorReason(String reason, String description) {
    this.setReason(reason);
    this.setDescription(description);
  }

  public String getReason() {
    return reason;
  }

  private void setReason(String reason) {
    this.reason = reason;
  }

  public String getDescription() {
    return description;
  }

  private void setDescription(String description) {
    this.description = description;
  }
}
