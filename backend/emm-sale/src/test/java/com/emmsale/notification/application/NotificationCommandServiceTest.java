package com.emmsale.notification.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.notification.application.dto.NotificationRequest;
import com.emmsale.notification.application.dto.NotificationResponse;
import com.emmsale.notification.domain.Notification;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NotificationCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private NotificationCommandService notificationCommandService;

  @Test
  @DisplayName("create() : 알림을 새로 생성할 수 있다.")
  void test_create() throws Exception {
    //given
    final long senderId = 1L;
    final long receiverId = 2L;
    final long eventId = 3L;
    final String message = "알림 메시지야";
    final long notificationId = 1L;

    final NotificationRequest request = new NotificationRequest(
        senderId,
        receiverId,
        message,
        eventId
    );

    final NotificationResponse actual = new NotificationResponse(
        notificationId, senderId, receiverId, message, eventId
    );

    //when
    final NotificationResponse expected = notificationCommandService.create(request);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(expected);
  }
}
