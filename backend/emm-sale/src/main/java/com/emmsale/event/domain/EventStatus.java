package com.emmsale.event.domain;

import static com.emmsale.event.exception.EventExceptionType.INVALID_STATUS;

import com.emmsale.event.exception.EventException;
import java.util.Arrays;
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

  public static EventStatus from(final String value) {
    return Arrays.stream(values())
        .filter(status -> status.isSameValue(value))
        .findFirst()
        .orElseThrow(() -> new EventException(INVALID_STATUS));
  }

  public boolean isSameValue(String value) {
    return this.value.equals(value);
  }
}
