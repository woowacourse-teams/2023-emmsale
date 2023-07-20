package com.emmsale.login.exception;

import com.emmsale.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum LoginExceptionType implements BaseExceptionType {

  NOT_FOUND_GITHUB_ACCESS_TOKEN(
      HttpStatus.BAD_REQUEST,
      "GitHub Access Token을 찾을 수 없습니다."
  ),
  INVALID_GITHUB_ACCESS_TOKEN(
      HttpStatus.NOT_FOUND,
      "GitHub Profile을 찾을 수 없습니다."
  ),
  INVALID_ACCESS_TOKEN(
      HttpStatus.BAD_REQUEST,
      "토큰이 유효하지 않습니다.."
  ),
  NOT_FOUND_AUTHORIZATION_TOKEN(
      HttpStatus.BAD_REQUEST,
      "인증 토큰을 찾을 수 없습니다."
  );

  private final HttpStatus httpStatus;
  private final String errorMessage;

  LoginExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
