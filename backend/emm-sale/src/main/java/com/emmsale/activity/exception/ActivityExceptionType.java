package com.emmsale.activity.exception;

import com.emmsale.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ActivityExceptionType implements BaseExceptionType {

  ALEADY_EXIST_ACTIVITY(
      HttpStatus.BAD_REQUEST,
      "이미 존재하는 활동입니다."
  );

  private final HttpStatus httpStatus;
  private final String errorMessage;

  ActivityExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
