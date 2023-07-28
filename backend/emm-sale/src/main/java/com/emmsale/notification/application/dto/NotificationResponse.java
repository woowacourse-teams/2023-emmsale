package com.emmsale.notification.application.dto;

import com.emmsale.notification.domain.Notification;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NotificationResponse {

  private final Long notificationId;
  private final Long senderId;
  private final Long receiverId;
  private final String message;
  private final Long eventId;

  public static NotificationResponse from(final Notification notification) {
    return new NotificationResponse(
        notification.getId(),
        notification.getSenderId(),
        notification.getReceiverId(),
        notification.getMessage(),
        notification.getEventId()
    );
  }
}
