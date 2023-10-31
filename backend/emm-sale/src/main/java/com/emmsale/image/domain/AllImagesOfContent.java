package com.emmsale.image.domain;

import static com.emmsale.image.exception.ImageExceptionType.NOT_FOUND_THUMBNAIL;
import static java.util.Comparator.comparing;

import com.emmsale.image.exception.ImageException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AllImagesOfContent {

  private final List<Image> images;

  public String extractThumbnailImage() {
    if (images.isEmpty()) {
      return null;
    }
    return images.stream()
        .filter(Image::isThumbnail)
        .findFirst()
        .orElseThrow(() -> new ImageException(NOT_FOUND_THUMBNAIL))
        .getName();
  }

  public List<String> extractInformationImages() {
    return images.stream()
        .sorted(comparing(Image::getOrder))
        .filter(Image::isNotThumbnail)
        .map(Image::getName)
        .collect(Collectors.toUnmodifiableList());
  }
}
