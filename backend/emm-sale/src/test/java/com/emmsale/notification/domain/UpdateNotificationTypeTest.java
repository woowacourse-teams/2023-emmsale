package com.emmsale.notification.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.emmsale.comment.domain.Comment;
import com.emmsale.event.domain.Event;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class UpdateNotificationTypeTest {

  static Stream<Arguments> convertClassTypeToEnum() {

    final String notificationType1 = Event.class.getName();
    final UpdateNotificationType type1 = UpdateNotificationType.EVENT;

    final String notificationType2 = Comment.class.getName();
    final UpdateNotificationType type2 = UpdateNotificationType.COMMENT;

    return Stream.of(
        Arguments.of(notificationType1, type1),
        Arguments.of(notificationType2, type2)
    );
  }

  @ParameterizedTest
  @MethodSource("convertClassTypeToEnum")
  @DisplayName("from() : 클래스 타입을 통해서 어떤 알림의 종류인지 알 수 있다.")
  void test_from(final String notificationType, final UpdateNotificationType type)
      throws Exception {
    //when & then
    assertEquals(UpdateNotificationType.from(notificationType), type);
  }
}
