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

  @ParameterizedTest
  @MethodSource("convertClassTypeToEnum")
  @DisplayName("from() : 클래스 타입을 통해서 어떤 알림의 종류인지 알 수 있다.")
  void test_from(final Class<?> classType, final UpdateNotificationType type) throws Exception {
    //when & then
    assertEquals(UpdateNotificationType.from(classType), type);
  }

  static Stream<Arguments> convertClassTypeToEnum() {

    final Class<?> classType1 = Event.class;
    final UpdateNotificationType type1 = UpdateNotificationType.EVENT;

    final Class<?> classType2 = Comment.class;
    final UpdateNotificationType type2 = UpdateNotificationType.COMMENT;

    return Stream.of(
        Arguments.of(classType1, type1),
        Arguments.of(classType2, type2)
    );
  }
}
