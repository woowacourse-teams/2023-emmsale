package com.emmsale.feed.application.dto;

import com.emmsale.feed.domain.Feed;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedListResponse {

  private final Long eventId;
  private final List<FeedSimpleResponse> feeds;

  public static FeedListResponse from(final Long eventId, final List<Feed> feeds) {
    final List<FeedSimpleResponse> feedSimpleResponses = feeds.stream()
        .map(FeedSimpleResponse::from)
        .collect(Collectors.toList());

    return new FeedListResponse(eventId, feedSimpleResponses);
  }
}
