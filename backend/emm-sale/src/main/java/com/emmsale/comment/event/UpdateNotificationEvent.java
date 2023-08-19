package com.emmsale.comment.event;

import com.emmsale.comment.domain.Comment;
import com.emmsale.event.domain.Event;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateNotificationEvent {

  private static final String UPDATE_NOTIFICATION_COMMENT_TYPE = "comment";
  private static final String UPDATE_NOTIFICATION_EVENT_TYPE = "event";

  private final Long receiverId;
  private final Long redirectId;
  private final String updateNotificationType;
  private final LocalDateTime createdAt;

  public static UpdateNotificationEvent of(final Comment comment, final Long redirectId) {
    return new UpdateNotificationEvent(
        comment.getMember().getId(),
        redirectId,
        UPDATE_NOTIFICATION_COMMENT_TYPE,
        LocalDateTime.now()
    );
  }

  public static UpdateNotificationEvent of(final Event event, final Long receiverId) {
    return new UpdateNotificationEvent(
        receiverId,
        event.getId(),
        UPDATE_NOTIFICATION_EVENT_TYPE,
        LocalDateTime.now()
    );
  }
}
