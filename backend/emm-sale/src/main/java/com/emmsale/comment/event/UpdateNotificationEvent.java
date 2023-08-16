package com.emmsale.comment.event;

import com.emmsale.comment.domain.Comment;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateNotificationEvent {

  private final Long receiverId;
  private final Long redirectId;
  private final String updateNotificationType;
  private final LocalDateTime createdAt;

  public static UpdateNotificationEvent from(final Comment comment) {
    return new UpdateNotificationEvent(
        comment.getMember().getId(),
        comment.getParentIdOrSelfId(),
        comment.getClass().getSimpleName(),
        LocalDateTime.now()
    );
  }
}
