package com.emmsale.event.exception;

import com.emmsale.base.BaseExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum EventExceptionType implements BaseExceptionType {

  EVENT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당하는 행사를 찾을 수 없습니다.");

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
