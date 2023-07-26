package com.emmsale.tag.exception;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;

public class TagException extends BaseException {

  private final TagExceptionType exceptionType;

  public TagException(final TagExceptionType exceptionType) {
    super(exceptionType.errorMessage());
    this.exceptionType = exceptionType;
  }

  @Override
  public BaseExceptionType exceptionType() {
    return exceptionType;
  }
}