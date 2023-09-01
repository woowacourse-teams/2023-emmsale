package com.emmsale.feed.application.dto;

import com.emmsale.feed.domain.Feed;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedSimpleResponse {

  public static final String DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss";

  private final Long id;
  private final String title;
  private final Long writerId;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime createdAt;

  public static FeedSimpleResponse from(final Feed feed) {
    return new FeedSimpleResponse(
        feed.getId(),
        feed.getTitle(),
        feed.getWriter().getId(),
        feed.getCreatedAt()
    );
  }
}
