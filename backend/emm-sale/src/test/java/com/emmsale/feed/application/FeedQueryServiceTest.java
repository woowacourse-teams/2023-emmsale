package com.emmsale.feed.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import com.emmsale.block.domain.Block;
import com.emmsale.block.domain.BlockRepository;
import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.feed.application.dto.FeedResponseRefactor;
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
import com.emmsale.feed.exception.FeedException;
import com.emmsale.feed.exception.FeedExceptionType;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.image.domain.Image;
import com.emmsale.image.domain.ImageType;
import com.emmsale.image.domain.repository.ImageRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
  @Autowired
  private BlockRepository blockRepository;
  @Autowired
  private ImageRepository imageRepository;

  private Feed feed1;
  private Event event;
  private Member writer;
  private Member reader;
  private Feed feed2;

  @BeforeEach
  void setUp() {
    event = eventRepository.save(EventFixture.인프콘_2023());
    writer = memberRepository.findById(1L).get();
    reader = memberRepository.findById(2L).get();
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

      final List<FeedResponseRefactor> expect = List.of(
          FeedResponseRefactor.of(feed1, Collections.emptyList(), 0L),
          FeedResponseRefactor.of(feed2, Collections.emptyList(), 0L)
      );

      //when
      final List<FeedResponseRefactor> actual = feedQueryService.findAllFeeds(writer, eventId);

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
      final List<FeedResponseRefactor> expect = List.of(
          FeedResponseRefactor.of(feed2, Collections.emptyList(), 0L)
      );

      //when
      final List<FeedResponseRefactor> actual = feedQueryService.findAllFeeds(writer, eventId);

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
          () -> feedQueryService.findAllFeeds(writer, 존재하지_않는_이벤트_id)
      );

      //then
      assertEquals(expect, actualException.exceptionType());
    }

    @Test
    @DisplayName("차단한 사용자의 피드는 조회되지 않는다.")
    void findAllFeedsWithBlockedMember() {
      //given
      final Feed feed3 = feedRepository.save(new Feed(event, reader, "피드3 제목", "피드3 내용"));
      blockRepository.save(new Block(reader.getId(), writer.getId()));

      final List<FeedResponseRefactor> expect = List.of(
          FeedResponseRefactor.of(feed3, Collections.emptyList(), 0L)
      );
      //when
      final List<FeedResponseRefactor> actual = feedQueryService
          .findAllFeeds(reader, event.getId());

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expect);
    }

    @Test
    @DisplayName("피드가 존재하지 않는 이벤트의 피드 목록을 조회한다.")
    void findAllFeedsWithNotExistFeed() {
      //given
      final Event noFeedEvent = eventRepository.save(EventFixture.구름톤());

      final List<FeedResponseRefactor> expect = Collections.emptyList();

      //when
      final List<FeedResponseRefactor> actual
          = feedQueryService.findAllFeeds(writer, noFeedEvent.getId());

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expect);
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

      final FeedResponseRefactor expect = FeedResponseRefactor.of(feed, Collections.emptyList(),
          0L);

      //when
      final FeedResponseRefactor actual = feedQueryService.findFeed(writer, feedId);

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
          () -> feedQueryService.findFeed(writer, notExistsFeedId)
      );

      //then
      assertEquals(expect, actualException.exceptionType());
    }

    @Test
    @DisplayName("삭제된 피드를 조회하면 FORBIDDEN_DELETED_FEED 타입의 FeedException이 발생한다.")
    void findFeedWithDeletedFeedIdTest() {
      //given
      feed1.delete();
      feedRepository.save(feed1);

      final long 삭제된_피드_id = feed1.getId();

      final FeedExceptionType expect = FeedExceptionType.FORBIDDEN_DELETED_FEED;

      //when
      final FeedException actualException = assertThrowsExactly(
          FeedException.class,
          () -> feedQueryService.findFeed(writer, 삭제된_피드_id)
      );

      //then
      assertEquals(expect, actualException.exceptionType());
    }
  }

  @Nested
  @DisplayName("자신이 작성한 피드 목록 조회 테스트")
  class FindAllMy {

    @Test
    @DisplayName("자신이 작성한 모든 피드를 조회한다.")
    void findAllFeedsTest() {
      //given
      final List<FeedResponseRefactor> expect = List.of(
          FeedResponseRefactor.of(feed1, Collections.emptyList(), 0L),
          FeedResponseRefactor.of(feed2, Collections.emptyList(), 0L)
      );

      //when
      final List<FeedResponseRefactor> actual = feedQueryService.findAllMyFeeds(writer);

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expect);
    }

    @Test
    @DisplayName("삭제된 피드는 자신이 작성한 피드 목록에 조회되지 않는다.")
    void findAllFeedsWithWithDeletedFeedTest() {
      //given
      feed1.delete();
      feedRepository.save(feed1);

      final List<FeedResponseRefactor> expect = List.of(
          FeedResponseRefactor.of(feed2, Collections.emptyList(), 0L)
      );

      //when
      final List<FeedResponseRefactor> actual = feedQueryService.findAllMyFeeds(writer);

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expect);
    }
  }

  @Nested
  @DisplayName("피드 이미지 조회 테스트")
  class FeedQueryWithImage {

    private Image image1;
    private Image image2;
    private Image image3;
    private List<Image> images;

    @BeforeEach
    void setUp() {
      image1 = new Image("image-uuid1", ImageType.FEED, feed1.getId(), 1, LocalDateTime.now());
      image2 = new Image("image-uuid2", ImageType.FEED, feed1.getId(), 2, LocalDateTime.now());
      image3 = new Image("image-uuid3", ImageType.FEED, feed1.getId(), 3, LocalDateTime.now());
      images = List.of(image1, image2, image3);

      imageRepository.saveAll(List.of(image2, image1, image3));
    }

    @Test
    @DisplayName("피드에 이미지가 있을 경우 피드 목록에서 이미지 리스트를 order순으로 정렬하여 함께 반환한다.")
    void findAllFeedsWithImages() {
      //given
      final Long eventId = event.getId();

      final List<FeedResponseRefactor> expect = List.of(
          FeedResponseRefactor.of(feed1, images, 0L),
          FeedResponseRefactor.of(feed2, Collections.emptyList(), 0L)
      );

      //when
      final List<FeedResponseRefactor> actual = feedQueryService.findAllMyFeeds(writer);

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expect);
    }

    @Test
    @DisplayName("피드에 이미지가 있을 경우 피드 목록에서 이미지 리스트를 order순으로 정렬하여 함께 반환한다.")
    void findDetailFeedWithImages() {
      //given
      final FeedResponseRefactor expect = FeedResponseRefactor.of(feed1, images, 0L);

      //when
      final FeedResponseRefactor actual = feedQueryService.findFeed(writer, feed1.getId());

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expect);
    }
  }
}
