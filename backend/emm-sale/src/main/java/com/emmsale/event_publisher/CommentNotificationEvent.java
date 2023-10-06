package com.emmsale.event_publisher;

import com.emmsale.comment.domain.Comment;
import com.emmsale.member.domain.Member;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class CommentNotificationEvent extends NotificationEvent {

  private static final String UPDATE_NOTIFICATION_COMMENT_TYPE = "COMMENT";

  private final String content;
  private final String writer;
  private final String writerImageUrl;

  public CommentNotificationEvent(
      final Long receiverId, final Long redirectId,
      final LocalDateTime createdAt, final String notificationType,
      final String content, final String writer,
      final String writerImageUrl
  ) {
    super(receiverId, redirectId, createdAt, notificationType);
    this.content = content;
    this.writer = writer;
    this.writerImageUrl = writerImageUrl;
  }

  public static CommentNotificationEvent of(final Comment comment, final Comment trigger) {
    final Member member = comment.getMember();
    final Member triggerMember = trigger.getMember();

    return new CommentNotificationEvent(
        member.getId(),
        trigger.getId(),
        LocalDateTime.now(),
        UPDATE_NOTIFICATION_COMMENT_TYPE,
        trigger.getContent(),
        triggerMember.getName(),
        triggerMember.getImageUrl()
    );
  }
}
