package com.emmsale.feed.application.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedListResponse {

  private final Long eventId;
  private final List<FeedSimpleResponse> feeds;
}
