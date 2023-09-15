package com.emmsale.image.domain;

public enum ImageType {
  FEED(10),
  EVENT(2);
  
  final private int maxImageCount;
  
  ImageType(final int maxImageCount) {
    this.maxImageCount = maxImageCount;
  }
  
  public boolean isOverMaxImageCount(final int imageCount) {
    return imageCount > maxImageCount;
  }
  
}
