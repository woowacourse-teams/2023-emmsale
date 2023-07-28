package com.emmsale.notification.application.dto;

import com.emmsale.notification.domain.NotificationStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NotificationModifyRequest {

  private final NotificationStatus updatedStatus;

  private NotificationModifyRequest() {
    this(null);
  }
}
