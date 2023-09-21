package com.emmsale.feed.application;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.feed.application.dto.FeedPostRequest;
import com.emmsale.feed.application.dto.FeedUpdateRequest;
import com.emmsale.feed.application.dto.FeedUpdateResponse;
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
import com.emmsale.feed.exception.FeedException;
import com.emmsale.feed.exception.FeedExceptionType;
import com.emmsale.image.application.ImageCommandService;
import com.emmsale.image.domain.ImageType;
import com.emmsale.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedCommandService {

  private final FeedRepository feedRepository;
  private final EventRepository eventRepository;
  private final ImageCommandService imageCommandService;

  public Long postFeed(
      final Member member,
      final FeedPostRequest feedPostRequest,
      final List<MultipartFile> images
  ) {
    final Event event = eventRepository.findById(feedPostRequest.getEventId())
        .orElseThrow(() -> new EventException(EventExceptionType.NOT_FOUND_EVENT));

    final Feed feed = new Feed(event, member, feedPostRequest.getTitle(),
        feedPostRequest.getContent());
    feedRepository.save(feed);

    saveImages(images, feed);

    return feed.getId();
  }

  private void saveImages(final List<MultipartFile> images, final Feed feed) {
    if (images != null && !images.isEmpty()) {
      imageCommandService.saveImages(ImageType.FEED, feed.getId(), images);
    }
  }

  public FeedUpdateResponse updateFeed(
      final Member member,
      final Long id,
      final FeedUpdateRequest request
  ) {
    final Feed feed = feedRepository.findById(id)
        .orElseThrow(() -> new FeedException(FeedExceptionType.NOT_FOUND_FEED));
    validateFeedOwner(member, feed);
    validateDeletedFeed(feed);

    final Event event = eventRepository.findById(request.getEventId())
        .orElseThrow(() -> new EventException(EventExceptionType.NOT_FOUND_EVENT));

    feed.updateFeed(event, request.getTitle(), request.getContent());

    return FeedUpdateResponse.from(feed);
  }

  public void deleteFeed(final Long id, final Member member) {
    final Feed feed = feedRepository.findById(id)
        .orElseThrow(() -> new FeedException(FeedExceptionType.NOT_FOUND_FEED));
    validateFeedOwner(member, feed);

    feed.delete();
  }

  private void validateFeedOwner(final Member member, final Feed feed) {
    if (feed.isNotOwner(member.getId())) {
      throw new FeedException(FeedExceptionType.FORBIDDEN_NOT_OWNER);
    }
  }

  private void validateDeletedFeed(final Feed feed) {
    if (feed.isDeleted()) {
      throw new FeedException(FeedExceptionType.FORBIDDEN_DELETED_FEED);
    }
  }
}
