package com.emmsale.notification.application.dto;

import com.emmsale.comment.domain.Comment;
import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.domain.UpdateNotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateNotificationResponse {

  private final Long updateNotificationId;
  private final Long receiverId;
  private final Long redirectId;
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private final LocalDateTime createdAt;
  private final UpdateNotificationType type;
  @JsonProperty(value = "isRead")
  private final boolean isRead;
  private final CommentTypeNotification commentTypeNotification;

  public static UpdateNotificationResponse convertCommentNotification(
      final UpdateNotification notification,
      final Comment comment
  ) {
    return new UpdateNotificationResponse(
        notification.getId(), notification.getReceiverId(),
        notification.getRedirectId(), notification.getCreatedAt(),
        notification.getUpdateNotificationType(), notification.isRead(),
        new CommentTypeNotification(
            comment.getContent(),
            comment.getEvent().getName(),
            comment.getMember().getImageUrl(),
            comment.getParentIdOrSelfId(),
            comment.getEvent().getId()
        )
    );
  }

  public static UpdateNotificationResponse convertEventNotification(
      final UpdateNotification notification
  ) {
    return new UpdateNotificationResponse(
        notification.getId(), notification.getReceiverId(),
        notification.getRedirectId(), notification.getCreatedAt(),
        notification.getUpdateNotificationType(), notification.isRead(),
        null
    );
  }

  @RequiredArgsConstructor
  @Getter
  public static class CommentTypeNotification {

    private final String content;
    private final String eventName;
    private final String commenterImageUrl;
    private final Long parentId;
    private final Long eventId;
  }

  private boolean getIsRead() {
    return isRead;
  }
}
