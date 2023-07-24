package com.emmsale.comment.application.dto;

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
}
