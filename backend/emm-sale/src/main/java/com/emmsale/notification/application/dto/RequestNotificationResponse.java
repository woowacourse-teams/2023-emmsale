package com.emmsale.notification.application.dto;

import com.emmsale.notification.domain.RequestNotification;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RequestNotificationResponse {

  private final Long notificationId;
  private final Long senderId;
  private final Long receiverId;
  private final String message;
  private final Long eventId;

  public static RequestNotificationResponse from(final RequestNotification requestNotification) {
    return new RequestNotificationResponse(
        requestNotification.getId(),
        requestNotification.getSenderId(),
        requestNotification.getReceiverId(),
        requestNotification.getMessage(),
        requestNotification.getEventId()
    );
  }
}
