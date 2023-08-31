package com.emmsale.feed.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.feed.application.dto.FeedPostRequest;
import com.emmsale.feed.application.dto.FeedPostResponse;
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.MemberFixture;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FeedCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private FeedCommandService feedCommandService;
  @Autowired
  private FeedRepository feedRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private EventRepository eventRepository;

  private Member 작성자;
  private Event 이벤트;

  @BeforeEach
  void setUp() {
    작성자 = memberRepository.save(MemberFixture.memberFixture());
    이벤트 = eventRepository.save(EventFixture.인프콘_2023());
  }

  @Test
  @DisplayName("피드를 작성한다.")
  void postFeedTest() {
    //given
    final String feedTitle = "피드 제목";
    final String feedContent = "피드 내용";
    final FeedPostRequest request = new FeedPostRequest(이벤트.getId(), feedTitle, feedContent);

    //when
    final FeedPostResponse expectResponse = feedCommandService.postFeed(작성자, request);
    final Feed actual = feedRepository.findById(expectResponse.getId()).get();
    final FeedPostResponse actualResponse = FeedPostResponse.from(actual);

    //then
    assertThat(actualResponse)
        .usingRecursiveComparison()
        .isEqualTo(actualResponse);
  }

  @Test
  @DisplayName("피드의 이벤트가 존재하지 않을 경우 EventException 타입의 NOT_FOUND_EVENT이 발생한다.")
  void postFeedWithNotExistEventTest() {
    //given
    final long 존재하지_않는_이벤트_id = 0L;
    final String feedTitle = "피드 제목";
    final String feedContent = "피드 내용";
    final FeedPostRequest request = new FeedPostRequest(존재하지_않는_이벤트_id, feedTitle, feedContent);

    final EventExceptionType expectExceptionType = EventExceptionType.NOT_FOUND_EVENT;

    //when
    final EventException actualException = assertThrowsExactly(EventException.class,
        () -> feedCommandService.postFeed(작성자, request));

    //then
    assertEquals(expectExceptionType, actualException.exceptionType());
  }
}
