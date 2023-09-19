package com.emmsale.feed.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedPostRequest {

  private final Long eventId;
  private final String title;
  private final String content;
}
