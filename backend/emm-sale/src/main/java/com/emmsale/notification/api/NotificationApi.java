package com.emmsale.notification.api;

import com.emmsale.notification.application.NotificationCommandService;
import com.emmsale.notification.application.dto.NotificationRequest;
import com.emmsale.notification.application.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationApi {

  private final NotificationCommandService notificationCommandService;

  @PostMapping("/notification")
  public NotificationResponse create(final NotificationRequest notificationRequest) {
    return notificationCommandService.create(notificationRequest);
  }
}
