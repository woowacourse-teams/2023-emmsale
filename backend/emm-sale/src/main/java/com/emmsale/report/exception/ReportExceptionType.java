package com.emmsale.report.exception;

import com.emmsale.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ReportExceptionType implements BaseExceptionType {

  NOT_FOUND_MEMBER(
      HttpStatus.NOT_FOUND,
      "존재하지 않는 사용자입니다."
  ),
  NOT_FOUND_CONTENT(
      HttpStatus.NOT_FOUND,
      "존재하지 않는 게시물입니다."
  ),
  REPORTED_MISMATCH_WRITER(
      HttpStatus.BAD_REQUEST,
      "신고한 게시물이 신고 대상자의 게시물이 아닙니다."
  ),
  FORBIDDEN_REPORT_MYSELF(
      HttpStatus.BAD_REQUEST,
      "자기 자신은 신고할 수 없습니다."
  ),
  REPORTER_MISMATCH(
      HttpStatus.FORBIDDEN,
      "신고 권한이 없습니다."
  ),
  ALREADY_EXIST_REPORT(
      HttpStatus.BAD_REQUEST,
      "이미 신고한 사용자입니다."
  );

  private final HttpStatus httpStatus;
  private final String errorMessage;

  ReportExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
