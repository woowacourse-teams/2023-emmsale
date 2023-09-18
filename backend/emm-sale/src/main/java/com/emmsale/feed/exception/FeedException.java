package com.emmsale.feed.exception;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;

public class FeedException extends BaseException {

  private final FeedExceptionType feedExceptionType;

  public FeedException(final FeedExceptionType feedExceptionType) {
    super(feedExceptionType.errorMessage());
    this.feedExceptionType = feedExceptionType;
  }

  @Override
  public BaseExceptionType exceptionType() {
    return feedExceptionType;
  }
}
