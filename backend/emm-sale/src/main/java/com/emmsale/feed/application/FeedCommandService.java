package com.emmsale.feed.application;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.feed.application.dto.FeedPostRequest;
import com.emmsale.feed.application.dto.FeedPostResponse;
import com.emmsale.feed.application.dto.FeedUpdateRequest;
import com.emmsale.feed.application.dto.FeedUpdateResponse;
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
import com.emmsale.feed.exception.FeedException;
import com.emmsale.feed.exception.FeedExceptionType;
import com.emmsale.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedCommandService {

  private final FeedRepository feedRepository;
  private final EventRepository eventRepository;

  public FeedPostResponse postFeed(final Member member, final FeedPostRequest request) {
    final Event event = eventRepository.findById(request.getEventId())
        .orElseThrow(() -> new EventException(EventExceptionType.NOT_FOUND_EVENT));

    final Feed feed = new Feed(event, member, request.getTitle(), request.getContent());
    final Feed savedFeed = feedRepository.save(feed);

    return FeedPostResponse.from(savedFeed);
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
