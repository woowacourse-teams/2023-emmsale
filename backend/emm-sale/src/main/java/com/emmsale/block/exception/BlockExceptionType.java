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
  ),
  BAD_REQUEST_SELF_BLOCK(
      HttpStatus.BAD_REQUEST,
      "자기 자신은 차단할 수 없습니다."
  ),
  NOT_FOUND_BLOCK(
      HttpStatus.NOT_FOUND,
      "요청한 차단 사용자를 찾을 수 없습니다."
  ),
  FORBBIDEN_UNREGISTER_BLOCK(
      HttpStatus.FORBIDDEN,
      "해당 차단에 대한 권한이 없습니다."
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
