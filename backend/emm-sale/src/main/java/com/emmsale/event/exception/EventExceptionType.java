package com.emmsale.event.exception;

import com.emmsale.base.BaseExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum EventExceptionType implements BaseExceptionType {

  NOT_FOUND_EVENT(HttpStatus.NOT_FOUND, "해당하는 행사를 찾을 수 없습니다."),
  FORBIDDEN_PARTICIPATE_EVENT(HttpStatus.FORBIDDEN, "참가하려는 사용자와 로그인된 사용자가 다릅니다."),
  NOT_FOUND_PARTICIPANT(HttpStatus.NOT_FOUND, "해당 행사에 등록되지 않은 사용자입니다."),
  ALREADY_PARTICIPATED(HttpStatus.BAD_REQUEST, "이미 참가신청한 멤버입니다."),
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
