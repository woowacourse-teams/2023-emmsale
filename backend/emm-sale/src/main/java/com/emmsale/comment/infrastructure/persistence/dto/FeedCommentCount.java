package com.emmsale.comment.infrastructure.persistence.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedCommentCount {

  private final Long feedId;
  private final Long count;
}
