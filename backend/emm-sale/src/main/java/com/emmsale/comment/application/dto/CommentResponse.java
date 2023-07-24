package com.emmsale.comment.application.dto;

import com.emmsale.comment.domain.Comment;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommentResponse {

  private final String content;
  private final Long commentId;
  private final Long parentId;
  private final Long eventId;
  private final boolean isDeleted;
  private final LocalDateTime createdAt;

  public static CommentResponse from(final Comment comment) {
    return new CommentResponse(
        comment.getContent(), comment.getId(),
        getParentId(comment), comment.getEvent().getId(),
        comment.isDeleted(), comment.getCreatedAt()
    );
  }

  private static Long getParentId(final Comment comment) {
    if (comment.getParent() == null) {
      return null;
    }
    return comment.getParent().getId();
  }
}
