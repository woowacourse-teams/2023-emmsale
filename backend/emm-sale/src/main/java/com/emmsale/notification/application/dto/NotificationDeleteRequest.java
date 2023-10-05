package com.emmsale.notification.application.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NotificationDeleteRequest {

  private final List<Long> deleteIds;

  private NotificationDeleteRequest() {
    this(null);
  }
}
