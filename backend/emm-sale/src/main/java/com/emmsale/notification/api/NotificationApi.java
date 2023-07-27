package com.emmsale.notification.api;

import com.emmsale.notification.application.NotificationCommandService;
import com.emmsale.notification.application.dto.FcmTokenRequest;
import com.emmsale.notification.application.dto.NotificationRequest;
import com.emmsale.notification.application.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationApi {

  private final NotificationCommandService notificationCommandService;

  @PostMapping("/notification")
  @ResponseStatus(HttpStatus.CREATED)
  public NotificationResponse create(@RequestBody final NotificationRequest notificationRequest) {
    return notificationCommandService.create(notificationRequest);
  }

  @PostMapping("/notification/token")
  public void createFcmToken(@RequestBody final FcmTokenRequest fcmTokenRequest) {
    notificationCommandService.createToken(fcmTokenRequest);
  }
}
