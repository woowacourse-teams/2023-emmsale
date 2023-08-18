package com.emmsale.notification.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.notification.application.dto.FcmTokenRequest;
import com.emmsale.notification.domain.FcmToken;
import com.emmsale.notification.domain.FcmTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FcmTokenRegisterServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private FcmTokenRepository fcmTokenRepository;
  @Autowired
  private FcmTokenRegisterService fcmTokenRegisterService;

  @Test
  @DisplayName("registerFcmToken() : 이미 FCM 토큰이 존재한다면 해당 멤버의 FCM 토큰을 변경할 수 있다.")
  void test_registerFcmToken_alreadyHasToken() throws Exception {
    //given
    final long memberId = 1L;
    final String token = "token";
    final String updateToken = "updateToken";
    fcmTokenRepository.save(new FcmToken(token, memberId));

    final FcmTokenRequest request = new FcmTokenRequest(updateToken, memberId);

    //when
    fcmTokenRegisterService.registerFcmToken(request);

    //then
    final FcmToken updatedFcmToken = fcmTokenRepository.findByMemberId(memberId).get();

    assertEquals(updateToken, updatedFcmToken.getToken());
  }

  @Test
  @DisplayName("registerFcmToken() : 해당 사용자의 FCM 토큰이 존재하지 않는다면 FCM 토큰을 저장한다.")
  void test_registerFcmToken_noToken() throws Exception {
    //given
    final long memberId = 1L;
    final String token = "token";

    final FcmTokenRequest request = new FcmTokenRequest(token, memberId);

    //when
    fcmTokenRegisterService.registerFcmToken(request);

    //then
    final FcmToken createdFcmToken = fcmTokenRepository.findByMemberId(memberId).get();

    assertAll(
        () -> assertEquals(token, createdFcmToken.getToken()),
        () -> assertEquals(memberId, createdFcmToken.getMemberId()),
        () -> assertNotNull(createdFcmToken.getId())
    );
  }
}
