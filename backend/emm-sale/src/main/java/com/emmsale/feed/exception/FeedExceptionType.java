package com.emmsale.feed.exception;

import com.emmsale.base.BaseExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FeedExceptionType implements BaseExceptionType {
  NOT_FOUND_FEED(HttpStatus.NOT_FOUND, "피드를 찾을 수 없습니다."),
  FORBIDDEN_NOT_OWNER(HttpStatus.FORBIDDEN, "피드의 소유자가 아닙니다."),
  FORBIDDEN_DELETED_FEED(HttpStatus.FORBIDDEN, "삭제된 피드입니다.");

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
