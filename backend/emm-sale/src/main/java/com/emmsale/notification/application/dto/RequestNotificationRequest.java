package com.emmsale.notification.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RequestNotificationRequest {

  private final Long senderId;
  private final Long receiverId;
  private final String message;
  private final Long eventId;
}
