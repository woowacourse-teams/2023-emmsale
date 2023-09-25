package com.emmsale.image.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ImageTypeTest {

  @ParameterizedTest
  @CsvSource(value = {"FEED:3:false", "FEED:6:true"}, delimiter = ':')
  @DisplayName("isOverMaxImageCount(): 입력받은 값이 이미지 유형의 최대 이미지 수보다 큰지 여부를 반환한다.")
  void isOverMaxImageCount(final ImageType type, final int imageCount, final boolean expected) {
    //given, when
    final boolean actual = type.isOverMaxImageCount(imageCount);

    //then
    assertThat(actual).isEqualTo(expected);
  }
}
