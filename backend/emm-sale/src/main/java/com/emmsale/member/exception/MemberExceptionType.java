package com.emmsale.member.exception;

import com.emmsale.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum MemberExceptionType implements BaseExceptionType {

  NOT_FOUND_MEMBER(
      HttpStatus.NOT_FOUND,
      "해당 멤버는 존재하지 않습니다."
  ),

  ALREADY_ONBOARDING(
      HttpStatus.BAD_REQUEST,
      "이미 온보딩을 완료한 사용자입니다."
  ),

  INVALID_ACTIVITY_IDS(
      HttpStatus.BAD_REQUEST,
      "요청한 activity id들 중에 유효하지 않은 값이 존재합니다"
  ),

  ALREADY_EXIST_ACTIVITY(
      HttpStatus.BAD_REQUEST,
      "이미 등록된 활동입니다."
  ),

  DUPLICATE_ACTIVITY(
      HttpStatus.BAD_REQUEST,
      "요청에 중복된 활동 ID가 포함되어 있습니다."
  ),
  ALREADY_EXIST_INTEREST_TAG(
      HttpStatus.BAD_REQUEST,
      "이미 관심 태그로 등록된 태그입니다."
  ),

  NOT_FOUND_INTEREST_TAG(
      HttpStatus.BAD_REQUEST,
      "관심 태그로 등록되지 않은 태그입니다."
  ),

  NULL_DESCRIPTION(
      HttpStatus.BAD_REQUEST,
      "한 줄 자기소개는 null이 될 수 없습니다."
  ),

  FORBIDDEN_DELETE_MEMBER(
      HttpStatus.FORBIDDEN,
      "해당 멤버를 삭제할 권한이 없습니다."
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
