package com.emmsale.feed.application;

import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.feed.application.dto.FeedPostRequest;
import com.emmsale.feed.application.dto.FeedPostResponse;
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
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
    final Long eventId = request.getEventId();
    validateEventExists(eventId);

    final Feed feed = new Feed(eventId, member, request.getTitle(), request.getContent());
    final Feed savedFeed = feedRepository.save(feed);

    return FeedPostResponse.from(savedFeed);
  }

  private void validateEventExists(final Long eventId) {
    final boolean isExistsEvent = eventRepository.existsById(eventId);
    if (!isExistsEvent) {
      throw new EventException(EventExceptionType.NOT_FOUND_EVENT);
    }
  }
}
