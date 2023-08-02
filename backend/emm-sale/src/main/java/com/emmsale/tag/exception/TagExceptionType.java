package com.emmsale.tag.exception;

import com.emmsale.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum TagExceptionType implements BaseExceptionType {

  NOT_FOUND_TAG(
      HttpStatus.NOT_FOUND,
      "해당 태그가 존재하지 않습니다."
  );

  private final HttpStatus httpStatus;
  private final String errorMessage;

  TagExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
