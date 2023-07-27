package com.emmsale.event.exception;

import com.emmsale.base.BaseExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum EventExceptionType implements BaseExceptionType {

  EVENT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당하는 행사를 찾을 수 없습니다."),
  INVALID_STATUS(
      HttpStatus.BAD_REQUEST,
      "요청하신 상태는 유효하지 않는 값입니다."
  ),
  INVALID_YEAR(
      HttpStatus.BAD_REQUEST,
      "연도 값은 2015 이상이어야 합니다."
  ),
  INVALID_MONTH(
      HttpStatus.BAD_REQUEST,
      "월은 1에서 12 사이여야 합니다."
  );

  private final HttpStatus httpStatus;
  private final String errorMessage;

  @Override
  public HttpStatus httpStatus() {
    return httpStatus;
  }

  @Override
  public String errorMessage() {
    return errorMessage;
  }
}
