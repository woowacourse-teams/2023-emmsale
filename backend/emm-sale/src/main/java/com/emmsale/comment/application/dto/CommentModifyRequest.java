package com.emmsale.comment.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommentModifyRequest {

  private final String content;

  private CommentModifyRequest() {
    this(null);
  }
}
