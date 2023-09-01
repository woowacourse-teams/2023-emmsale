package com.emmsale.message_room.exception;

import com.emmsale.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum MessageExceptionType implements BaseExceptionType {

  SENDER_IS_NOT_EQUAL_REQUEST_MEMBER(HttpStatus.FORBIDDEN,
      "로그인한 사용자와 메세지를 보내는 사용자가 다릅니다.");

  private final HttpStatus httpStatus;
  private final String errorMessage;

  MessageExceptionType(final HttpStatus httpStatus, final String errorMessage) {
    this.httpStatus = httpStatus;
    this.errorMessage = errorMessage;
  }

  @Override
  public HttpStatus httpStatus() {
    return null;
  }

  @Override
  public String errorMessage() {
    return null;
  }
}
