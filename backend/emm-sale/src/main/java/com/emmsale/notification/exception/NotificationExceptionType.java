package com.emmsale.notification.exception;

import com.emmsale.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum NotificationExceptionType implements BaseExceptionType {

  BAD_REQUEST_MEMBER_ID(
      HttpStatus.BAD_REQUEST,
      "해당 ID를 가진 멤버는 존재하지 않습니다."
  )
  ;

  private final HttpStatus httpStatus;
  private final String errorMessage;

  NotificationExceptionType(final HttpStatus httpStatus, final String errorMessage) {
    this.httpStatus = httpStatus;
    this.errorMessage = errorMessage;
  }

  @Override
  public HttpStatus httpStatus() {
    return httpStatus;
  }

  @Override
  public String errorMessage() {
    return errorMessage;
  }
}
