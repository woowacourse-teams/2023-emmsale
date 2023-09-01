package com.emmsale.feed.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.feed.application.dto.FeedListResponse;
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

  @Test
  @DisplayName("이벤트를 기준으로 모든 피드를 조회한다.")
  void findAllFeedsTest() {
    //given
    final Long eventId = event.getId();
    final List<Feed> feeds = List.of(feed1, feed2);
    final FeedListResponse expect = FeedListResponse.from(eventId, feeds);

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
    final EventException actualException = Assertions.assertThrowsExactly(
        EventException.class,
        () -> feedQueryService.findAllFeeds(존재하지_않는_이벤트_id)
    );

    //then
    assertEquals(expect, actualException.exceptionType());
  }
}
