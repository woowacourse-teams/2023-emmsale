package com.emmsale.comment.application.dto;

import com.emmsale.comment.domain.Comment;
import com.emmsale.event.domain.Event;
import com.emmsale.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CommentResponse {

  private static final String DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss";

  private String content;
  private Long commentId;
  private Long parentId;
  private Long eventId;
  private String eventName;
  private boolean isDeleted;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private LocalDateTime createdAt;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private LocalDateTime updatedAt;
  private Long memberId;
  private String memberImageUrl;
  private String memberName;

  public static CommentResponse from(final Comment comment) {
    final Member member = comment.getMember();
    final Event event = comment.getEvent();

    return new CommentResponse(
        comment.getContent(), comment.getId(),
        getParentId(comment), event.getId(),
        event.getName(), comment.isDeleted(),
        comment.getCreatedAt(), comment.getUpdatedAt(),
        member.getId(), member.getImageUrl(),
        member.getName()
    );
  }

  public static CommentResponse from(final Comment comment, final List<Long> blockedMemberIds) {
    final Member member = comment.getMember();
    final Event event = comment.getEvent();

    return new CommentResponse(
        comment.getContentOrHideIfBlockedMember(blockedMemberIds), comment.getId(),
        getParentId(comment), event.getId(),
        event.getName(), comment.isDeleted(),
        comment.getCreatedAt(), comment.getUpdatedAt(),
        member.getId(), member.getImageUrl(),
        member.getName()
    );
  }

  private static Long getParentId(final Comment comment) {
    return comment.getParent()
        .map(Comment::getId)
        .orElse(null);
  }

}
