package com.emmsale.activity.exception;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;

public class ActivityException extends BaseException {

  private final ActivityExceptionType exceptionType;

  public ActivityException(final ActivityExceptionType exceptionType) {
    super(exceptionType.errorMessage());
    this.exceptionType = exceptionType;
  }

  @Override
  public BaseExceptionType exceptionType() {
    return exceptionType;
  }
}
