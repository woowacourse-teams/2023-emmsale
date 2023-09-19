package com.emmsale.event.domain;

import lombok.Getter;

@Getter
public enum EventMode {

  ONLINE("온라인"),
  OFFLINE("오프라인"),
  ON_OFFLINE("온오프라인");

  private final String value;

  EventMode(final String value) {
    this.value = value;
  }
}
