package com.emmsale.comment.application.dto;

import com.emmsale.feed.domain.Feed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FeedCommentCount {

  private final Feed feed;
  private final long count;
}
