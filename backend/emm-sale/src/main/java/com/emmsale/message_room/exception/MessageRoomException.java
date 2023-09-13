package com.emmsale.message_room.exception;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;

public class MessageRoomException extends BaseException {

  private final MessageRoomExceptionType exceptionType;

  public MessageRoomException(final MessageRoomExceptionType exceptionType) {
    super(exceptionType.errorMessage());
    this.exceptionType = exceptionType;
  }

  @Override
  public BaseExceptionType exceptionType() {
    return exceptionType;
  }
}
