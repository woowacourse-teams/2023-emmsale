package com.emmsale.image;

import com.emmsale.image.domain.Image;
import com.emmsale.image.domain.ImageType;
import java.time.LocalDateTime;

public class ImageFixture {

  public static Image 행사_이미지1(final Long eventId) {
    return new Image(
        "이미지1", ImageType.EVENT, eventId, 0, LocalDateTime.of(2023, 8, 15, 15, 0));
  }

  public static Image 행사_이미지2(final Long eventId) {
    return new Image(
        "이미지2", ImageType.EVENT, eventId, 1, LocalDateTime.of(2023, 8, 15, 15, 0));
  }

  public static Image 행사_이미지3(final Long eventId) {
    return new Image(
        "이미지3", ImageType.EVENT, eventId, 2, LocalDateTime.of(2023, 8, 15, 15, 0));
  }

  public static Image 행사_이미지4(final Long eventId) {
    return new Image(
        "이미지4", ImageType.EVENT, eventId, 3, LocalDateTime.of(2023, 8, 15, 15, 0));
  }
}
