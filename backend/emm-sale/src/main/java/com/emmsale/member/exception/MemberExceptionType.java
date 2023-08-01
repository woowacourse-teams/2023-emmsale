package com.emmsale.member.exception;

import com.emmsale.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum MemberExceptionType implements BaseExceptionType {

  NOT_FOUND_MEMBER(
      HttpStatus.NOT_FOUND,
      "해당 멤버는 존재하지 않습니다."
  ),

  INVALID_ACTIVITY_IDS(
      HttpStatus.BAD_REQUEST,
      "요청한 activity id들 중에 유효하지 않은 값이 존재합니다"
  ),
  NULL_DESCRIPTION(
      HttpStatus.BAD_REQUEST,
      "한 줄 자기소개는 null이 될 수 없습니다."
  ),

  OVER_LENGTH_DESCRIPTION(
      HttpStatus.BAD_REQUEST,
      "한줄 자기소개에 입력 가능한 글자 수를 초과했습니다."
  );

  private final HttpStatus httpStatus;
  private final String errorMessage;

  MemberExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
