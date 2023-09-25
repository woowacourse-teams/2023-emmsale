package com.emmsale.image.domain;

public enum ImageType {
  FEED(5),
  EVENT(0);

  private static final int NO_LIMIT_COUNT = 0;
  private final int maxImageCount;

  ImageType(final int maxImageCount) {
    this.maxImageCount = maxImageCount;
  }

  public boolean isOverMaxImageCount(final int imageCount) {
    if (maxImageCount == NO_LIMIT_COUNT) {
      return false;
    }
    return imageCount > maxImageCount;
  }

}
