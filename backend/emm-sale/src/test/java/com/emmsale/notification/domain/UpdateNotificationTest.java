package com.emmsale.notification.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UpdateNotificationTest {

  @ParameterizedTest
  @CsvSource({
      "EVENT, false",
      "COMMENT, true"
  })
  @DisplayName("isCommentNotification() : COMMENT 관련 알림일 경우 true를 반환한다.")
  void test_isCommentNotification(final UpdateNotificationType type, final boolean result)
      throws Exception {
    //given
    final UpdateNotification notification = new UpdateNotification(
        1L,
        2L,
        type,
        LocalDateTime.now()
    );

    //when & then
    assertEquals(notification.isCommentNotification(), result);
  }

  @ParameterizedTest
  @CsvSource({
      "EVENT, true",
      "COMMENT, false"
  })
  @DisplayName("isEventNotification() : EVENT 관련 알림일 경우 true를 반환한다.")
  void test_isEventNotification(final UpdateNotificationType type, final boolean result)
      throws Exception {
    //given
    final UpdateNotification notification = new UpdateNotification(
        1L,
        2L,
        type,
        LocalDateTime.now()
    );

    //when & then
    assertEquals(notification.isEventNotification(), result);
  }
}
