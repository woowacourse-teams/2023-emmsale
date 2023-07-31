package com.emmsale.event.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class EventStatusTest {

  @ParameterizedTest
  @CsvSource(value = {"진행 중,IN_PROGRESS", "진행 예정,UPCOMING", "종료된 행사,ENDED"}, delimiter = ',')
  @DisplayName("동일한 문자 값을 갖는 EventStatus가 존재하면 해당 enum 객체를 반환한다.")
  void from_success(String input, EventStatus expected) {
    // given, when
    final EventStatus actual = EventStatus.from(input);

    // then
    assertThat(actual).isEqualTo(expected);

  }

  @ParameterizedTest
  @ValueSource(strings = {"진행중", "진행예정", "아마란스", "진행 중 "})
  @DisplayName("동일한 문자 값을 갖는 EventStatus가 존재하지 않으면 예외를 반환한다.")
  void from_fail(String input) {
    // given, when
    final ThrowingCallable actual = () -> EventStatus.from(input);

    // then
    assertThatThrownBy(actual)
        .isInstanceOf(EventException.class)
        .hasMessage(EventExceptionType.INVALID_STATUS.errorMessage());
  }
}
