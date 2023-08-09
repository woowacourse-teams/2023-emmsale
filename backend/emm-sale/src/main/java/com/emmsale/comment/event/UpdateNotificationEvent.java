package com.emmsale.comment.event;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class UpdateNotificationEvent {

  private final Long receiverId;
  private final Long redirectId;
  private final String updateNotificationType;
  private final LocalDateTime createdAt;

  public UpdateNotificationEvent(
      final Long receiverId,
      final Long redirectId,
      final String updateNotificationType
  ) {
    this.receiverId = receiverId;
    this.redirectId = redirectId;
    this.updateNotificationType = updateNotificationType;
    this.createdAt = LocalDateTime.now();
  }
}
