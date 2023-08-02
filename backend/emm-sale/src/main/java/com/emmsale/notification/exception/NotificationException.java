package com.emmsale.notification.exception;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;

public class NotificationException extends BaseException {

  private final NotificationExceptionType exceptionType;

  public NotificationException(final NotificationExceptionType exceptionType) {
    super(exceptionType.errorMessage());
    this.exceptionType = exceptionType;
  }

  @Override
  public BaseExceptionType exceptionType() {
    return exceptionType;
  }
}
