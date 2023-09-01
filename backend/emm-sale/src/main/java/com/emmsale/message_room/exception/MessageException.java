package com.emmsale.message_room.exception;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;

public class MessageException extends BaseException {

  private final MessageExceptionType exceptionType;

  public MessageException(final MessageExceptionType type) {
    super(type.errorMessage());
    this.exceptionType = type;
  }

  @Override
  public BaseExceptionType exceptionType() {
    return exceptionType;
  }
}
