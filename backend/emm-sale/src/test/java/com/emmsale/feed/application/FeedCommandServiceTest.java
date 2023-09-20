package com.emmsale.feed.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.emmsale.event.EventFixture;
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
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.image.application.ImageCommandService;
import com.emmsale.image.domain.ImageType;
import com.emmsale.member.MemberFixture;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

class FeedCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private FeedCommandService feedCommandService;
  @Autowired
  private FeedRepository feedRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private EventRepository eventRepository;
  @MockBean
  private ImageCommandService imageCommandService;

  private Member 작성자;
  private Event 이벤트1;
  private Event 이벤트2;

  @BeforeEach
  void setUp() {
    작성자 = memberRepository.save(MemberFixture.memberFixture());
    이벤트1 = eventRepository.save(EventFixture.인프콘_2023());
    이벤트2 = eventRepository.save(EventFixture.구름톤());
  }

  @Test
  @DisplayName("피드를 작성한다.")
  void postFeedTest() {
    //given
    final String feedTitle = "피드 제목";
    final String feedContent = "피드 내용";
    final FeedPostRequest request = new FeedPostRequest(이벤트1.getId(), feedTitle, feedContent);

    //when
    final FeedPostResponse expectResponse = feedCommandService.postFeed(작성자, request,
        Collections.emptyList());
    final Feed actual = feedRepository.findById(expectResponse.getId()).get();
    final FeedPostResponse actualResponse = FeedPostResponse.from(actual);

    //then
    assertThat(expectResponse)
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
        () -> feedCommandService.postFeed(작성자, request, Collections.emptyList()));

    //then
    assertEquals(expectExceptionType, actualException.exceptionType());
  }

  @Nested
  @DisplayName("피드 업데이트 테스트")
  class UpdateFeed {

    private Feed 피드;
    private Long newEventId;
    private String newTitle;
    private String newContent;

    @BeforeEach
    void setUp() {
      피드 = feedRepository.save(new Feed(이벤트1, 작성자, "피드 제목", "피드 내용"));
      newEventId = 이벤트2.getId();
      newTitle = "새로운 제목";
      newContent = "새로운 내용";
    }

    @Test
    @DisplayName("피드를 성공적으로 업데이트한다.")
    void updateFeedTest() {
      //given
      final FeedUpdateRequest request = new FeedUpdateRequest(newEventId, newTitle, newContent);

      //when
      final FeedUpdateResponse expect = feedCommandService.updateFeed(작성자, 피드.getId(),
          request);
      final Feed updatedFeed = feedRepository.findById(피드.getId()).get();
      final FeedUpdateResponse actual = FeedUpdateResponse.from(updatedFeed);

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expect);
    }

    @Test
    @DisplayName("업데이트하려는 피드의 id가 존재하지 않을 경우 NOT_FOUND_FEED 타입의 FeedException이 발생한다.")
    void updateFeedWithNotExistFeedIdTest() {
      //given
      final long 존재하지_않는_피드_id = 0L;
      final FeedUpdateRequest request = new FeedUpdateRequest(newEventId, newTitle, newContent);

      final FeedExceptionType expect = FeedExceptionType.NOT_FOUND_FEED;

      //when
      final FeedException actualException = assertThrowsExactly(
          FeedException.class,
          () -> feedCommandService.updateFeed(작성자, 존재하지_않는_피드_id, request)
      );

      //then
      assertEquals(expect, actualException.exceptionType());
    }

    @Test
    @DisplayName("업데이트하려는 피드가 삭제되었 경우 FORBIDDEN_DELETED_FEED 타입의 FeedException이 발생한다.")
    void updateFeedWithDeletedFeedIdTest() {
      //given
      피드.delete();
      feedRepository.save(피드);

      final long 삭제된_피드_id = 피드.getId();
      final FeedUpdateRequest request = new FeedUpdateRequest(newEventId, newTitle, newContent);

      final FeedExceptionType expect = FeedExceptionType.FORBIDDEN_DELETED_FEED;

      //when
      final FeedException actualException = assertThrowsExactly(
          FeedException.class,
          () -> feedCommandService.updateFeed(작성자, 삭제된_피드_id, request)
      );

      //then
      assertEquals(expect, actualException.exceptionType());
    }

    @Test
    @DisplayName("피드의 작성자와 업데이트하려는 유저가 다를 경우 FORBIDDEN_NOT_OWNER 타입의 FeedException이 발생한다.")
    void updateFeedWithNotFeedWriterTest() {
      //given
      final Member 작성자가_아닌_사용자 = memberRepository.save(
          new Member(111L, "image-url", "github-username"));
      final FeedUpdateRequest request = new FeedUpdateRequest(newEventId, newTitle, newContent);

      final FeedExceptionType expect = FeedExceptionType.FORBIDDEN_NOT_OWNER;

      //when
      final FeedException actualException = assertThrowsExactly(FeedException.class,
          () -> feedCommandService.updateFeed(작성자가_아닌_사용자, 피드.getId(), request));

      //then
      assertEquals(expect, actualException.exceptionType());
    }

    @Test
    @DisplayName("피드의 업데이트하려는 이벤트가 존재하지 않을 경우 EventException 타입의 NOT_FOUND_EVENT이 발생한다.")
    void updateFeedWithNotExistEventTest() {
      //given
      final long 존재하지_않는_이벤트_id = 0L;
      final FeedUpdateRequest request = new FeedUpdateRequest(존재하지_않는_이벤트_id, newTitle, newContent);

      final EventExceptionType expect = EventExceptionType.NOT_FOUND_EVENT;

      //when
      final EventException actualException = assertThrowsExactly(EventException.class,
          () -> feedCommandService.updateFeed(작성자, 피드.getId(), request));

      //then
      assertEquals(expect, actualException.exceptionType());
    }
  }

  @Nested
  @DisplayName("피드 삭제 테스트")
  class FeedDelete {

    private Feed 피드;

    @BeforeEach
    void setUp() {
      피드 = feedRepository.save(new Feed(이벤트1, 작성자, "피드 제목", "피드 내용"));
    }

    @Test
    @DisplayName("피드를 삭제한다.")
    void deleteFeedTest() {
      //given
      final Long feedId = 피드.getId();

      //when
      feedCommandService.deleteFeed(feedId, 작성자);

      //then
      final Feed actualFeed = feedRepository.findById(feedId).get();
      assertTrue(actualFeed.isDeleted());
    }

    @Test
    @DisplayName("삭제하려는 피드의 id가 존재하지 않을 경우 NOT_FOUND_FEED 타입의 FeedException이 발생한다.")
    void deleteFeedWithNotExistIdTest() {
      //given
      final long 존재하지_않는_피드_id = 0L;

      final FeedExceptionType expect = FeedExceptionType.NOT_FOUND_FEED;

      //when
      final FeedException actualException = assertThrowsExactly(
          FeedException.class,
          () -> feedCommandService.deleteFeed(존재하지_않는_피드_id, 작성자)
      );

      //then
      assertEquals(expect, actualException.exceptionType());
    }

    @Test
    @DisplayName("피드의 작성자와 삭제하려는 유저가 다를 경우 FORBIDDEN_NOT_OWNER 타입의 FeedException이 발생한다.")
    void deleteFeedWithNotFeedWriterTest() {
      //given
      final Member 작성자가_아닌_사용자 = memberRepository.save(
          new Member(111L, "image-url", "github-username")
      );

      final FeedExceptionType expect = FeedExceptionType.FORBIDDEN_NOT_OWNER;

      //when
      final FeedException actualException = assertThrowsExactly(
          FeedException.class,
          () -> feedCommandService.deleteFeed(피드.getId(), 작성자가_아닌_사용자)
      );

      //then
      assertEquals(expect, actualException.exceptionType());
    }
  }

  @Nested
  @DisplayName("피드 이미지 업로드 테스트")
  class PostFeedWithImage {

    @Test
    @DisplayName("피드에 이미지를 함께 업로드하면 imageCommandService가 호출된다.")
    void postFeedWithImages() {
      //given
      final String feedTitle = "피드 제목";
      final String feedContent = "피드 내용";
      final FeedPostRequest request = new FeedPostRequest(이벤트1.getId(), feedTitle, feedContent);

      //when
      feedCommandService.postFeed(작성자, request,
          List.of(new MockMultipartFile("image", "image".getBytes())));

      //then
      verify(imageCommandService, times(1))
          .saveImages(any(ImageType.class), any(Long.class), any(List.class));
    }

    @Test
    @DisplayName("피드에 이미지가 비어있으면 imageCommandService가 호출되지 않는다.")
    void postFeedWithoutImages() {
      //given
      final String feedTitle = "피드 제목";
      final String feedContent = "피드 내용";
      final FeedPostRequest request = new FeedPostRequest(이벤트1.getId(), feedTitle, feedContent);

      //when
      feedCommandService.postFeed(작성자, request, Collections.emptyList());

      //then
      verify(imageCommandService, times(0))
          .saveImages(any(ImageType.class), any(Long.class), any(List.class));
    }
  }
}
