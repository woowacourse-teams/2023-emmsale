package com.emmsale.comment.application.dto;

import com.emmsale.comment.domain.Comment;
import com.emmsale.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CommentResponse {

  private String content;
  private Long commentId;
  private Long parentId;
  private Long eventId;
  private boolean isDeleted;
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private LocalDateTime createdAt;
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private LocalDateTime updatedAt;
  private Long memberId;
  private String memberImageUrl;
  private String memberName;

  public static CommentResponse from(final Comment comment) {
    final Member member = comment.getMember();
    return new CommentResponse(
        comment.getContent(), comment.getId(),
        getParentId(comment), comment.getEvent().getId(),
        comment.isDeleted(), comment.getCreatedAt(),
        comment.getUpdatedAt(), member.getId(),
        member.getImageUrl(), member.getName()
    );
  }

  private static Long getParentId(final Comment comment) {
    if (comment.isRootComment()) {
      return null;
    }
    return comment.getParent().getId();
  }
}
