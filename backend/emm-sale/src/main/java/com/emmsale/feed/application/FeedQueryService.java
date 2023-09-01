package com.emmsale.feed.application;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.feed.application.dto.FeedListResponse;
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeedQueryService {

  private final FeedRepository feedRepository;
  private final EventRepository eventRepository;

  public FeedListResponse findAllFeeds(final Long eventId) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(EventExceptionType.NOT_FOUND_EVENT));

    final List<Feed> feeds = feedRepository.findAllByEvent(event);

    return FeedListResponse.from(eventId, feeds);
  }
}
