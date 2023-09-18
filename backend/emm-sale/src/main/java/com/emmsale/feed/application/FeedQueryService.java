package com.emmsale.feed.application;

import com.emmsale.block.domain.Block;
import com.emmsale.block.domain.BlockRepository;
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
import com.emmsale.member.domain.Member;
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
  private final BlockRepository blockRepository;
  private final CommentDao commentDao;

  public FeedListResponse findAllFeeds(final Member member, final Long eventId) {
    validateEvent(eventId);

    final List<Feed> feeds = excludeBlockedMembersFeed(member,
        feedRepository.findAllByEventIdAndNotDeleted(eventId));
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

  private List<Feed> excludeBlockedMembersFeed(final Member member, final List<Feed> feeds) {
    final List<Long> blockedMemberIds = getBlockedMemberIds(member);
    return feeds.stream()
        .filter(feed -> isNotBlockedMember(blockedMemberIds, feed))
        .collect(Collectors.toList());
  }

  private boolean isNotBlockedMember(final List<Long> blockedMemberIds, final Feed feed) {
    return !blockedMemberIds.contains(feed.getWriter().getId());
  }

  private List<Long> getBlockedMemberIds(final Member member) {
    return blockRepository.findAllByRequestMemberId(member.getId())
        .stream()
        .map(Block::getBlockMemberId)
        .collect(Collectors.toList());
  }

  public FeedDetailResponse findFeed(final Member member, final Long id) {
    final Feed feed = feedRepository.findById(id)
        .orElseThrow(() -> new FeedException(FeedExceptionType.NOT_FOUND_FEED));
    validateBlockedMemberFeed(member, feed);

    validateDeletedFeed(feed);

    return FeedDetailResponse.from(feed);
  }

  private void validateBlockedMemberFeed(final Member member, final Feed feed) {
    if (blockRepository.existsByRequestMemberIdAndBlockMemberId(member.getId(),
        feed.getWriter().getId())) {
      throw new FeedException(FeedExceptionType.CAN_NOT_ACCESS_BLOCKED_MEMBER_FEED);
    }
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
