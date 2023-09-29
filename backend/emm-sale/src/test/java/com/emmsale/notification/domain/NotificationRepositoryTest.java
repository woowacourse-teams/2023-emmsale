package com.emmsale.notification.domain;

import com.emmsale.helper.JpaRepositorySliceTestHelper;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NotificationRepositoryTest extends JpaRepositorySliceTestHelper {

  @Autowired
  private NotificationRepository notificationRepository;

  private String commentJsonData1, commentJsonData2, eventJsonData1;

  @BeforeEach
  void setUp() {
    commentJsonData1 = "{"
        + "\"content\":\"content\","
        + "\"writer\":\"writer\","
        + "\"writerImageUrl\":\"imageUrl\""
        + "}";

    eventJsonData1 = "{"
        + "\"title\":\"title\""
        + "}";

    commentJsonData2 = "{"
        + "\"content\":\"content\","
        + "\"writer\":\"writer\","
        + "\"writerImageUrl\":\"imageUrl\""
        + "}";
  }

  @Test
  @DisplayName("findAllByReceiverId() : 특정 사용자가 받은 알림 목록들을 조회할 수 있다.")
  void test_findAllByReceiverId() throws Exception {
    //given
    final long receiverId = 26L;

    final Notification notification1 = notificationRepository.save(
        new Notification(NotificationType.COMMENT, receiverId, 3333L, LocalDateTime.now(),
            commentJsonData1)
    );

    final Notification notification2 = notificationRepository.save(
        new Notification(NotificationType.EVENT, receiverId, 3333L, LocalDateTime.now(),
            eventJsonData1)
    );

    notificationRepository.save(
        new Notification(NotificationType.COMMENT, receiverId, 3333L, LocalDateTime.now(),
            commentJsonData2));

    final List<Notification> expect = List.of(notification1, notification2);

    //when
    final List<Notification> actual = notificationRepository.findAllByReceiverId(receiverId);

    //then
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }
}
