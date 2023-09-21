package com.emmsale.feed.application.dto;

import com.emmsale.feed.domain.Feed;
import com.emmsale.image.domain.Image;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedSimpleResponse {

  private static final String DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss";

  private final Long id;
  private final String title;
  private final String content;
  private final Long writerId;
  private final List<String> images;
  private final Long commentCount;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime createdAt;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime updatedAt;

  public static FeedSimpleResponse from(final Feed feed, final List<Image> images,
      final Long commentCount) {
    final List<String> imageUrls = images.stream()
        .map(Image::getName)
        .collect(Collectors.toList());

    return new FeedSimpleResponse(
        feed.getId(),
        feed.getTitle(),
        feed.getContent(),
        feed.getWriter().getId(),
        imageUrls,
        commentCount,
        feed.getCreatedAt(),
        feed.getUpdatedAt()
    );
  }
}
