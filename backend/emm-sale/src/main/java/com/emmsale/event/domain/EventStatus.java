package com.emmsale.event.domain;

import lombok.Getter;

@Getter
public enum EventStatus {

  IN_PROGRESS("진행 중"),
  UPCOMING("진행 예정"),
  ENDED("종료된 행사");

  private final String value;

  EventStatus(final String value) {
    this.value = value;
  }

  public boolean isSameValue(String value) {
    return this.value.equals(value);
  }
}
