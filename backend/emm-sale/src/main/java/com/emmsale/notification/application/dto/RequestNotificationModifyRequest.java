package com.emmsale.notification.application.dto;

import com.emmsale.notification.domain.RequestNotificationStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RequestNotificationModifyRequest {

  private final RequestNotificationStatus updatedStatus;

  private RequestNotificationModifyRequest() {
    this(null);
  }
}
