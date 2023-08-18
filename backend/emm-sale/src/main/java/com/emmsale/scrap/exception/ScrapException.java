package com.emmsale.scrap.exception;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;

public class ScrapException extends BaseException {

  private final ScrapExceptionType exceptionType;

  public ScrapException(final ScrapExceptionType exceptionType) {
    super(exceptionType.errorMessage());
    this.exceptionType = exceptionType;
  }

  @Override
  public BaseExceptionType exceptionType() {
    return exceptionType;
  }
}
