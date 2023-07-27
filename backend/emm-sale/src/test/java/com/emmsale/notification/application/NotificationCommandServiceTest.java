package com.emmsale.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.notification.application.dto.FcmTokenRequest;
import com.emmsale.notification.application.dto.NotificationRequest;
import com.emmsale.notification.application.dto.NotificationResponse;
import com.emmsale.notification.domain.FcmToken;
import com.emmsale.notification.domain.FcmTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NotificationCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private NotificationCommandService notificationCommandService;
  @Autowired
  private FcmTokenRepository fcmTokenRepository;

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

  @Test
  @DisplayName("createToken() : 이미 FCM 토큰이 존재한다면 해당 멤버의 FCM 토큰을 변경할 수 있다.")
  void test_createToken_alreadyHasToken() throws Exception {
    //given
    final long memberId = 1L;
    final String token = "token";
    final String updateToken = "updateToken";
    fcmTokenRepository.save(new FcmToken(token, memberId));

    final FcmTokenRequest request = new FcmTokenRequest(updateToken, memberId);

    //when
    notificationCommandService.createToken(request);

    //then
    final FcmToken updatedFcmToken = fcmTokenRepository.findByMemberId(memberId).get();

    assertEquals(updateToken, updatedFcmToken.getToken());
  }
}
