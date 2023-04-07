package com.logispin.wallet.api.error;

import java.io.Serializable;
import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class LogicalError implements Serializable {

  private static final long serialVersionUID = -9018521964625746776L;

  private String error;
}
