package com.emmsale.event.exception;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;

public class EventException extends BaseException {

  private final EventExceptionType eventExceptionType;

  public EventException(final EventExceptionType eventExceptionType) {
    super(eventExceptionType.errorMessage());
    this.eventExceptionType = eventExceptionType;
  }

  @Override
  public BaseExceptionType exceptionType() {
    return eventExceptionType;
  }
}
