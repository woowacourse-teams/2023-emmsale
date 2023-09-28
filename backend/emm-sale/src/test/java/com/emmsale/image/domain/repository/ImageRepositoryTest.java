package com.emmsale.image.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.helper.JpaRepositorySliceTestHelper;
import com.emmsale.image.domain.Image;
import com.emmsale.image.domain.ImageType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ImageRepositoryTest extends JpaRepositorySliceTestHelper {

  @Autowired
  private ImageRepository imageRepository;
  private Image image1;
  private Image image2;
  private Image image3;
  private Image image4;
  private Image image5;
  private Image image6;

  @BeforeEach
  void setUp() {
    image1 = new Image("name1", ImageType.FEED, 1L, 1, LocalDateTime.now());
    image2 = new Image("name2", ImageType.FEED, 1L, 2, LocalDateTime.now());
    image3 = new Image("name3", ImageType.FEED, 2L, 1, LocalDateTime.now());
    image4 = new Image("name4", ImageType.EVENT, 1L, 0, LocalDateTime.now());
    image5 = new Image("name5", ImageType.EVENT, 1L, 1, LocalDateTime.now());
    image6 = new Image("name6", ImageType.EVENT, 2L, 0, LocalDateTime.now());

    imageRepository.saveAll(List.of(image1, image2, image3, image4, image5, image6));
  }

  @Test
  @DisplayName("복수 개의 행사 id들에 해당하는 섬네일 이미지들을 조회할 수 있다.")
  void findAllThumbnailByEventIdIn() {
    //given
    final List<Image> expect = List.of(image4, image6);

    //when
    final List<Image> actual = imageRepository.findAllThumbnailByEventIdIn(List.of(1L, 2L));

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);

  }

  @Test
  @DisplayName("복수개의 피드 id들에 해당하는 이미지들을 조회할 수 있다.")
  void findAllByFeedIdInTest() {
    //given
    final List<Image> expect = List.of(image1, image2, image3);

    //when
    final List<Image> actual = imageRepository.findAllByFeedIdIn(List.of(1L, 2L));

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }

  @Test
  @DisplayName("하나의 피드 id에 해당하는 이미지들을 조회할 수 있다.")
  void findAllByFeedIdTest() {
    //given
    final List<Image> expect = List.of(image1, image2);

    //when
    final List<Image> actual = imageRepository.findAllByFeedId(image2.getContentId());

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }
}
