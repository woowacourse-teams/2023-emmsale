package com.emmsale.notification.api;

import com.emmsale.notification.application.FcmTokenRegisterService;
import com.emmsale.notification.application.dto.FcmTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FcmTokenApi {

  private final FcmTokenRegisterService fcmTokenRegisterService;

  @PostMapping("/notifications/token")
  public void createFcmToken(@RequestBody final FcmTokenRequest fcmTokenRequest) {
    fcmTokenRegisterService.registerFcmToken(fcmTokenRequest);
  }
}
