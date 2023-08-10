package com.emmsale.scrap.exception;

import com.emmsale.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ScrapExceptionType implements BaseExceptionType {
  ALREADY_EXIST_SCRAP(
      HttpStatus.BAD_REQUEST,
      "이미 존재하는 스크랩입니다."
  ),
  NOT_FOUND_SCRAP(
      HttpStatus.NOT_FOUND,
      "스크랩이 존재하지 않습니다."
  ),
  FORBIDDEN_NOT_OWNER(
      HttpStatus.FORBIDDEN,
      "삭제할 권한이 없습니다."
  );

  private final HttpStatus httpStatus;
  private final String errorMessage;

  ScrapExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
