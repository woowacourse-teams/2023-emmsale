package com.emmsale.event.domain;

import lombok.Getter;

@Getter
public enum PaymentType {

  PAID("유료"),
  FREE("무료"),
  FREE_PAID("유무료");

  private final String value;

  PaymentType(final String value) {
    this.value = value;
  }
}
