package com.logispin.wallet.api.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Errors {

  @JsonProperty("Errors")
  private final ErrorBlock errorBlock;
}
