package com.emmsale.comment.application.dto;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommentAddRequest {

  private final String content;
  private final Long eventId;
  private final Long parentId;

  public Optional<Long> optionalParentId() {
    return Optional.ofNullable(parentId);
  }
}
