package com.emmsale.feed.application;

import com.emmsale.comment.infrastructure.persistence.CommentDao;
import com.emmsale.comment.infrastructure.persistence.dto.FeedCommentCount;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
  private final CommentDao commentDao;

  public FeedListResponse findAllFeeds(final Long eventId) {
    validateEvent(eventId);

    final List<Feed> feeds = feedRepository.findAllByEventIdAndNotDeleted(eventId);
    final List<Long> feedIds = feeds.stream()
        .map(Feed::getId)
        .collect(Collectors.toList());
    final Map<Long, Long> feedCommentCounts = commentDao.findCommentCountByFeedIds(feedIds).stream()
        .collect(Collectors.toMap(
            FeedCommentCount::getFeedId,
            FeedCommentCount::getCount
        ));

    final Map<Feed, Long> commentCountByFeed = feeds.stream()
        .collect(Collectors.toMap(
            Function.identity(),
            feed -> feedCommentCounts.getOrDefault(feed.getId(), DEFAULT_COMMENT_COUNT)
        ));

    final List<FeedSimpleResponse> feedSimpleResponses = commentCountByFeed.entrySet().stream()
        .map(FeedSimpleResponse::from)
        .sorted(Comparator.comparing(FeedSimpleResponse::getUpdatedAt).reversed())
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
