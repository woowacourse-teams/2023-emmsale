package com.emmsale.feed.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
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
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FeedQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private FeedQueryService feedQueryService;
  @Autowired
  private FeedRepository feedRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private MemberRepository memberRepository;

  private Feed feed1;
  private Event event;
  private Member writer;
  private Feed feed2;

  @BeforeEach
  void setUp() {
    event = eventRepository.save(EventFixture.인프콘_2023());
    writer = memberRepository.findById(1L).get();
    feed1 = feedRepository.save(new Feed(event, writer, "피드1 제목", "피드1 내용"));
    feed2 = feedRepository.save(new Feed(event, writer, "피드2 제목", "피드2 내용"));
  }

  @Nested
  @DisplayName("피드 목록 조회 테스트")
  class FindAll {

    @Test
    @DisplayName("이벤트를 기준으로 모든 피드를 조회한다.")
    void findAllFeedsTest() {
      //given
      final Long eventId = event.getId();
      final Map<Feed, Long> feedCommentCountMap = Map.of(feed1, 0L, feed2, 0L);
      final List<FeedSimpleResponse> feedSimpleResponses = feedCommentCountMap.entrySet().stream()
          .map(FeedSimpleResponse::from)
          .collect(Collectors.toList());
      final FeedListResponse expect = new FeedListResponse(eventId, feedSimpleResponses);
      expect.getFeeds().sort(Comparator.comparing(FeedSimpleResponse::getUpdatedAt).reversed());

      //when
      final FeedListResponse actual = feedQueryService.findAllFeeds(eventId);

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expect);
    }

    @Test
    @DisplayName("이벤트를 기준으로 모든 피드를 조회할 때 삭제된 피드는 조회되지 않는다.")
    void findAllFeedsWithWithDeletedFeedTest() {
      //given
      feed1.delete();
      feedRepository.save(feed1);

      final Long eventId = event.getId();
      final Map<Feed, Long> feedCommentCountMap = Map.of(feed2, 0L);
      final List<FeedSimpleResponse> feedSimpleResponses = feedCommentCountMap.entrySet().stream()
          .map(FeedSimpleResponse::from)
          .collect(Collectors.toList());
      final FeedListResponse expect = new FeedListResponse(eventId, feedSimpleResponses);

      //when
      final FeedListResponse actual = feedQueryService.findAllFeeds(eventId);

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expect);
    }

    @Test
    @DisplayName("존재하지 않는 이벤트의 피드를 조회하면 NOT_FOUND_EVENT 타입의 EventException이 발생한다.")
    void findAllFeedsWithNotExistsEventTest() {
      //given
      final long 존재하지_않는_이벤트_id = 0L;
      final EventExceptionType expect = EventExceptionType.NOT_FOUND_EVENT;

      //when
      final EventException actualException = assertThrowsExactly(
          EventException.class,
          () -> feedQueryService.findAllFeeds(존재하지_않는_이벤트_id)
      );

      //then
      assertEquals(expect, actualException.exceptionType());
    }

  }

  @Nested
  @DisplayName("피드 상세 조회 테스트")
  class FindFeed {

    @Test
    @DisplayName("피드의 상세 내용을 조회한다.")
    void findFeedTest() {
      //given
      final Feed feed = feed1;
      final Long feedId = feed.getId();

      final FeedDetailResponse expect = FeedDetailResponse.from(feed);

      //when
      final FeedDetailResponse actual = feedQueryService.findFeed(feedId);

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expect);
    }

    @Test
    @DisplayName("존재하지 않는 피드를 조회하면 NOT_FOUND_FEED 타입의 FeedException이 발생한다.")
    void findFeedWithNotExistsFeedIdTest() {
      //given;
      final long notExistsFeedId = 0L;

      final FeedExceptionType expect = FeedExceptionType.NOT_FOUND_FEED;

      //when
      final FeedException actualException = assertThrowsExactly(
          FeedException.class,
          () -> feedQueryService.findFeed(notExistsFeedId)
      );

      //then
      assertEquals(expect, actualException.exceptionType());
    }

    @Test
    @DisplayName("삭제된 피드를 조회하면 FORBIDDEN_DELETED_FEED 타입의 FeedException이 발생한다.")
    void findFeedWithDeletedFeedIdTest() {
      //given;
      feed1.delete();
      feedRepository.save(feed1);

      final long 삭제된_피드_id = feed1.getId();

      final FeedExceptionType expect = FeedExceptionType.FORBIDDEN_DELETED_FEED;

      //when
      final FeedException actualException = assertThrowsExactly(
          FeedException.class,
          () -> feedQueryService.findFeed(삭제된_피드_id)
      );

      //then
      assertEquals(expect, actualException.exceptionType());
    }
  }
}
