package com.emmsale.feed.application;

import com.emmsale.comment.application.dto.FeedCommentCount;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.feed.application.dto.FeedDetailResponse;
import com.emmsale.feed.application.dto.FeedListResponse;
import com.emmsale.feed.application.dto.FeedSimpleResponse;
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
import com.emmsale.feed.exception.FeedException;
import com.emmsale.feed.exception.FeedExceptionType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FeedQueryService {

  private static final long DEFAULT_COMMENT_COUNT = 0L;

  private final FeedRepository feedRepository;
  private final EventRepository eventRepository;
  private final CommentRepository commentRepository;

  public FeedListResponse findAllFeeds(final Long eventId) {
    validateEvent(eventId);

    final List<Feed> feeds = feedRepository.findAllByEventIdAndNotDeleted(eventId);
    final Map<Feed, Long> feedCommentCountMap = commentRepository.countByFeedIn(feeds).stream()
        .collect(Collectors.toMap(FeedCommentCount::getFeed, FeedCommentCount::getCount));

    feeds.forEach(feed -> {
      if (!feedCommentCountMap.containsKey(feed)) {
        feedCommentCountMap.put(feed, DEFAULT_COMMENT_COUNT);
      }
    });

    final List<FeedSimpleResponse> feedSimpleResponses = feedCommentCountMap.entrySet().stream()
        .map(FeedSimpleResponse::from)
        .collect(Collectors.toList());

    return new FeedListResponse(eventId, feedSimpleResponses);
  }

  public FeedDetailResponse findFeed(final Long id) {
    final Feed feed = feedRepository.findById(id)
        .orElseThrow(() -> new FeedException(FeedExceptionType.NOT_FOUND_FEED));

    validateDeletedFeed(feed);

    return FeedDetailResponse.from(feed);
  }

  private void validateEvent(final Long eventId) {
    if (!eventRepository.existsById(eventId)) {
      throw new EventException(EventExceptionType.NOT_FOUND_EVENT);
    }
  }

  private void validateDeletedFeed(final Feed feed) {
    if (feed.isDeleted()) {
      throw new FeedException(FeedExceptionType.FORBIDDEN_DELETED_FEED);
    }
  }
}
