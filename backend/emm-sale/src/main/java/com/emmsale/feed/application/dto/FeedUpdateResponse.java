package com.emmsale.feed.application.dto;

import com.emmsale.feed.domain.Feed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedUpdateResponse {

  private final Long id;
  private final Long eventId;
  private final Long writerId;
  private final String title;
  private final String content;

  public static FeedUpdateResponse from(final Feed feed) {
    return new FeedUpdateResponse(
        feed.getId(),
        feed.getEventId(),
        feed.getWriter().getId(),
        feed.getTitle(),
        feed.getContent()
    );
  }
}
