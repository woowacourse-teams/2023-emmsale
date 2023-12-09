package com.emmsale.image.application;

import static com.emmsale.event.EventFixture.인프콘_2023;
import static com.emmsale.member.MemberFixture.memberFixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
import com.emmsale.feed.exception.FeedException;
import com.emmsale.feed.exception.FeedExceptionType;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.image.domain.Image;
import com.emmsale.image.domain.ImageType;
import com.emmsale.image.domain.repository.ImageRepository;
import com.emmsale.image.exception.ImageException;
import com.emmsale.image.exception.ImageExceptionType;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class ImageCommandServiceTest extends ServiceIntegrationTestHelper {

  private ImageCommandService imageCommandService;
  private ImageCommandService imageCommandServiceWithMockImageRepository;
  @Autowired
  private ImageRepository imageRepository;
  private ImageRepository mockImageRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private FeedRepository feedRepository;
  @Autowired
  private MemberRepository memberRepository;
  private S3Client s3Client;

  @BeforeEach
  void setUp() {
    s3Client = mock(S3Client.class);
    mockImageRepository = mock(ImageRepository.class);
    imageCommandService = new ImageCommandService(
        s3Client,
        imageRepository,
        eventRepository,
        feedRepository
    );
    imageCommandServiceWithMockImageRepository = new ImageCommandService(
        s3Client,
        mockImageRepository,
        eventRepository,
        feedRepository
    );
  }

  @Nested
  @DisplayName("saveImages() 메서드를 호출하면 S3와 DB에 이미지를 업로드한다.")
  class SaveImages {

    @Test
    @DisplayName("S3와 DB에 Image를 성공적으로 업로드할 수 있다.")
    void saveImages_success() {
      //given
      final Event event = eventRepository.save(인프콘_2023());
      final List<Image> expected = List.of(
          new Image("테스트테스트.png", ImageType.EVENT, event.getId(), 0, null),
          new Image("테스트테스트2.png", ImageType.EVENT, event.getId(), 1, null));
      final List<String> imageNames = List.of("테스트테스트.png", "테스트테스트2.png");
      final List<MultipartFile> files = List.of(
          new MockMultipartFile("test", "test.png", "", new byte[]{}),
          new MockMultipartFile("test", "test.png", "", new byte[]{}));

      BDDMockito.given(s3Client.uploadImages(any()))
          .willReturn(imageNames);

      //when
      imageCommandService.saveImages(ImageType.EVENT, event.getId(), files);
      final List<Image> actual = imageRepository.findAll();

      //then
      assertAll(
          () -> assertThat(actual)
              .usingRecursiveComparison()
              .ignoringFields("id", "createdAt")
              .isEqualTo(expected),
          () -> verify(s3Client, times(1))
              .uploadImages(any())
      );
    }

    @Test
    @DisplayName("이미지를 추가하려는 행사가 존재하지 않는 행사인 경우 예외를 던진다.")
    void saveImages_fail_not_found_event() {
      //given
      final Long noExistEventId = 999L;
      final List<MultipartFile> files = List.of(
          new MockMultipartFile("test", "test.png", "", new byte[]{}),
          new MockMultipartFile("test", "test.png", "", new byte[]{}));

      //when
      final ThrowingCallable actual = () -> imageCommandService.saveImages(ImageType.EVENT,
          noExistEventId, files);

      //then
      assertThatThrownBy(actual).isInstanceOf(EventException.class)
          .hasMessage(EventExceptionType.NOT_FOUND_EVENT.errorMessage());
    }

    @Test
    @DisplayName("이미지를 추가하려는 행사가 존재하지 않는 피드인 경우 예외를 던진다.")
    void saveImages_fail_not_found_feed() {
      //given
      final Long noExistFeedId = 999L;
      final List<MultipartFile> files = List.of(
          new MockMultipartFile("test", "test.png", "", new byte[]{}),
          new MockMultipartFile("test", "test.png", "", new byte[]{}));

      //when
      final ThrowingCallable actual = () -> imageCommandService.saveImages(ImageType.FEED,
          noExistFeedId, files);

      //then
      assertThatThrownBy(actual).isInstanceOf(FeedException.class)
          .hasMessage(FeedExceptionType.NOT_FOUND_FEED.errorMessage());
    }

    @Test
    @DisplayName("추가하려는 이미지의 개수가 컨텐츠의 최대 이미지 개수보다 크면 예외를 던진다.")
    void saveImages_fail_over_max_image_count() {
      //given
      final Event event = eventRepository.save(인프콘_2023());
      final Member member = memberRepository.save(memberFixture());
      final Feed feed = feedRepository.save(new Feed(event, member, "피드", "피드 내용"));
      final List<MultipartFile> files = List.of(
          new MockMultipartFile("test", "test1.png", "", new byte[]{}),
          new MockMultipartFile("test", "test2.png", "", new byte[]{}),
          new MockMultipartFile("test", "test3.png", "", new byte[]{}),
          new MockMultipartFile("test", "test4.png", "", new byte[]{}),
          new MockMultipartFile("test", "test5.png", "", new byte[]{}),
          new MockMultipartFile("test", "test6.png", "", new byte[]{}));

      //when
      final ThrowingCallable actual = () -> imageCommandService.saveImages(ImageType.FEED,
          feed.getId(), files);

      //then
      assertThatThrownBy(actual).isInstanceOf(ImageException.class)
          .hasMessage(ImageExceptionType.OVER_MAX_IMAGE_COUNT.errorMessage());
    }
  }

  @Nested
  @DisplayName("deleteImages() 메서드를 호출하면 특정 컨텐츠의 이미지들을 S3와 DB에서 삭제한다.")
  class DeleteImages {

    @Test
    @DisplayName("S3와 DB에서 Image를 성공적으로 삭제할 수 있다.")
    void deleteImages_success() {
      //givend
      final Event event = eventRepository.save(인프콘_2023());
      final List<Image> expected = List.of(
          new Image("테스트테스트.png", ImageType.EVENT, event.getId(), 0, null),
          new Image("테스트테스트2.png", ImageType.EVENT, event.getId(), 1, null));
      imageRepository.saveAll(expected);

      BDDMockito.willDoNothing().given(s3Client).deleteImages(any());

      //when
      imageCommandService.deleteImages(ImageType.EVENT, event.getId());
      final List<Image> actual = imageRepository.findAll();

      //then
      assertAll(
          () -> assertThat(actual).isEmpty(),
          () -> verify(s3Client, times(1))
              .deleteImages(any())
      );
    }
  }
}
