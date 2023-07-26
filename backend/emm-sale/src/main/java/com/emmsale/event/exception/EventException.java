package com.emmsale.event.exception;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;

public class EventException extends BaseException {

  private final EventExceptionType exceptionType;

  public EventException(final EventExceptionType exceptionType) {
    super(exceptionType.errorMessage());
    this.exceptionType = exceptionType;
  }

  @Override
  public BaseExceptionType exceptionType() {
    return exceptionType;
  }
}
