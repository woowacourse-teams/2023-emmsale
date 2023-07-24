package com.emmsale.comment.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommentAddRequest {

  private final String content;
  private final Long eventId;
  private final Long parentId;
}
