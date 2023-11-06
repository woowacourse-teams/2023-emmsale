package com.emmsale.feed.application.dto;

import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.feed.domain.Feed;
import com.emmsale.image.domain.Image;
import com.emmsale.member.application.dto.MemberReferenceResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FeedResponseRefactor {

  private static final String DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss";

  private final Long id;
  private final Long eventId;
  private final String title;
  private final String content;
  private final MemberReferenceResponse writer;
  private final List<String> images;
  private final Long commentCount;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime createdAt;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime updatedAt;

  public static FeedResponseRefactor of(
      final Feed feed,
      final List<Image> images,
      final Long commentCount
  ) {
    final List<String> imageUrls = images.stream()
        .map(Image::getName)
        .collect(toUnmodifiableList());

    return new FeedResponseRefactor(
        feed.getId(),
        feed.getEvent().getId(),
        feed.getTitle(),
        feed.getContent(),
        MemberReferenceResponse.from(feed.getWriter()),
        imageUrls,
        commentCount,
        feed.getCreatedAt(),
        feed.getUpdatedAt()
    );
  }
}
