package com.emmsale.event_publisher;

import com.emmsale.comment.domain.Comment;
import com.emmsale.member.domain.Member;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommentNotificationEvent {

  private static final String UPDATE_NOTIFICATION_COMMENT_TYPE = "COMMENT";

  private final Long receiverId;
  private final Long redirectId;
  private final LocalDateTime createdAt;
  private final String notificationType;
  private final String content;
  private final String writer;
  private final String writerImageUrl;

  public static CommentNotificationEvent of(final Comment comment, final Long redirectId) {
    final Member member = comment.getMember();

    return new CommentNotificationEvent(
        member.getId(),
        redirectId,
        LocalDateTime.now(),
        UPDATE_NOTIFICATION_COMMENT_TYPE,
        comment.getContent(),
        member.getName(),
        member.getImageUrl()
    );
  }

}
