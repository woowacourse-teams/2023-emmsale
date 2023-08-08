package com.emmsale.block.exception;

import com.emmsale.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum BlockExceptionType implements BaseExceptionType {

  NOT_FOUND_MEMBER(
      HttpStatus.NOT_FOUND,
      "사용자를 찾을 수 없습니다."
  ),
  ALREADY_BLOCKED_MEMBER(
      HttpStatus.BAD_REQUEST,
      "이미 차단한 사용자입니다."
  );

  private final HttpStatus httpStatus;
  private final String errorMessage;

  BlockExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
