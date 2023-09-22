package com.emmsale.feed.application.dto;

import com.emmsale.feed.domain.Feed;
import com.emmsale.image.domain.Image;
import com.emmsale.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FeedDetailResponse {

  private static final String DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss";

  private final Long id;
  private final Long eventId;
  private final WriterProfileResponse writer;
  private final String title;
  private final String content;
  private final List<String> images;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime createdAt;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime updatedAt;

  public static FeedDetailResponse from(final Feed feed, final List<Image> images) {
    final WriterProfileResponse writerResponse = WriterProfileResponse.from(feed.getWriter());
    final List<String> imageUrls = images.stream()
        .map(Image::getName)
        .collect(Collectors.toList());

    return new FeedDetailResponse(
        feed.getId(),
        feed.getEvent().getId(),
        writerResponse,
        feed.getTitle(),
        feed.getContent(),
        imageUrls,
        feed.getCreatedAt(),
        feed.getUpdatedAt()
    );
  }

  @Getter
  @RequiredArgsConstructor
  public static class WriterProfileResponse {

    private final Long memberId;
    private final String name;
    private final String imageUrl;

    public static WriterProfileResponse from(final Member writer) {
      return new WriterProfileResponse(
          writer.getId(),
          writer.getName(),
          writer.getImageUrl()
      );
    }
  }
}
