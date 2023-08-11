package com.emmsale.event.exception;

import com.emmsale.base.BaseExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum EventExceptionType implements BaseExceptionType {

  NOT_FOUND_EVENT(HttpStatus.NOT_FOUND, "해당하는 행사를 찾을 수 없습니다."),
  FORBIDDEN_CREATE_RECRUITMENT_POST(
      HttpStatus.FORBIDDEN,
      "참가 게시글을 작성하려는 사용자와 로그인된 사용자가 다릅니다."
  ),
  NOT_FOUND_RECRUITMENT_POST(HttpStatus.NOT_FOUND, "행사 참가 게시글을 찾을 수 없습니다."),
  ALREADY_CREATE_RECRUITMENT_POST(
      HttpStatus.BAD_REQUEST,
      "이미 참가신청 게시글을 작성한 사용자입니다."
  ),
  INVALID_STATUS(
      HttpStatus.BAD_REQUEST,
      "요청하신 상태는 유효하지 않는 값입니다."
  ),
  INVALID_DATE_FORMAT(
      HttpStatus.BAD_REQUEST,
      "날짜 형식이 올바르지 않습니다."
  ),
  NOT_FOUND_TAG(
      HttpStatus.NOT_FOUND,
      "해당하는 태그를 찾을 수 없습니다."
  ),
  SUBSCRIPTION_START_AFTER_EVENT_START(
      HttpStatus.BAD_REQUEST,
      "신청 시작일은 행사 시작일 이후일 수 없습니다."
  ),
  SUBSCRIPTION_END_AFTER_EVENT_END(
      HttpStatus.BAD_REQUEST,
      "신청 마감일은 행사 마감일 이후일 수 없습니다."
  ),
  SUBSCRIPTION_START_AFTER_SUBSCRIPTION_END(
      HttpStatus.BAD_REQUEST,
      "행사의 신청 시작일이 신청 마감일 이후일 수 없습니다."
  ),
  START_DATE_TIME_AFTER_END_DATE_TIME(
      HttpStatus.BAD_REQUEST,
      "행사의 시작 일시가 마감 일시 이후일 수 없습니다."
  ),
  START_DATE_AFTER_END_DATE(
      HttpStatus.BAD_REQUEST,
      "조회하려는 날짜 범위의 시작일이 마감일 이후일 수 없습니다."
  ),
  RECRUITMENT_POST_NOT_BELONG_EVENT(
      HttpStatus.BAD_REQUEST,
      "참가 게시글이 행사에 속하지 않는 게시글입니다."
  ),

  FORBIDDEN_UPDATE_RECRUITMENT_POST(
      HttpStatus.FORBIDDEN,
      "참가 게시글을 업데이트할 수 있는 권한이 없습니다."
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
