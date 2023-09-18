package com.emmsale.image.domain;

public enum ImageType {
  FEED(5),
  EVENT(2);
  
  private final int maxImageCount;
  
  ImageType(final int maxImageCount) {
    this.maxImageCount = maxImageCount;
  }
  
  public boolean isOverMaxImageCount(final int imageCount) {
    return imageCount > maxImageCount;
  }
  
}
