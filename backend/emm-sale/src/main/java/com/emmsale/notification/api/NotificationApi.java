package com.emmsale.notification.api;

import com.emmsale.notification.application.NotificationCommandService;
import com.emmsale.notification.application.NotificationQueryService;
import com.emmsale.notification.application.dto.FcmTokenRequest;
import com.emmsale.notification.application.dto.NotificationModifyRequest;
import com.emmsale.notification.application.dto.NotificationRequest;
import com.emmsale.notification.application.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationApi {

  private final NotificationCommandService notificationCommandService;
  private final NotificationQueryService notificationQueryService;

  @PostMapping("/notifications")
  @ResponseStatus(HttpStatus.CREATED)
  public NotificationResponse create(@RequestBody final NotificationRequest notificationRequest) {
    return notificationCommandService.create(notificationRequest);
  }

  @PostMapping("/notifications/token")
  public void createFcmToken(@RequestBody final FcmTokenRequest fcmTokenRequest) {
    notificationCommandService.createToken(fcmTokenRequest);
  }

  @PatchMapping("/notifications/{notification-id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void modify(
      @RequestBody final NotificationModifyRequest notificationModifyRequest,
      @PathVariable("notification-id") final Long notificationId
  ) {
    notificationCommandService.modify(notificationModifyRequest, notificationId);
  }

  @GetMapping("/notifications/{notification-id}")
  public NotificationResponse find(
      @PathVariable("notification-id") final Long notificationId
  ) {
    return notificationQueryService.findNotificationBy(notificationId);
  }
}
