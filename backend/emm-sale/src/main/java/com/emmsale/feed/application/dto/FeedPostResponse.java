package com.emmsale.feed.application.dto;

import com.emmsale.feed.domain.Feed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedPostResponse {

  private final Long id;
  private final Long eventId;
  private final Long writerId;
  private final String title;
  private final String content;

  public static FeedPostResponse from(final Feed feed) {
    return new FeedPostResponse(
        feed.getId(),
        feed.getEvent().getId(),
        feed.getWriter().getId(),
        feed.getTitle(),
        feed.getContent()
    );
  }
}
