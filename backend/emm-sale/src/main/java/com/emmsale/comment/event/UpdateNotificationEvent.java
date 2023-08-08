package com.emmsale.comment.event;

import com.emmsale.notification.domain.UpdateNotificationType;
import lombok.Getter;

@Getter
public class UpdateNotificationEvent {

  private final Long receiverId;
  private final Long redirectId;
  private final UpdateNotificationType updateNotificationType;

  public UpdateNotificationEvent(
      final Long receiverId,
      final Long redirectId,
      final UpdateNotificationType updateNotificationType
  ) {
    this.receiverId = receiverId;
    this.redirectId = redirectId;
    this.updateNotificationType = updateNotificationType;
  }
}
