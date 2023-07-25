package com.emmsale.comment.exception;

import com.emmsale.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum CommentExceptionType implements BaseExceptionType {

  NOT_FOUND_COMMENT(
      HttpStatus.NOT_FOUND,
      "해당 댓글은 존재하지 않습니다."
  ),

  CAN_NOT_DELETE_COMMENT(
      HttpStatus.FORBIDDEN,
      "댓글 작성자 외에는 삭제할 수 없습니다."
  )

  ;

  private final HttpStatus httpStatus;
  private final String errorMessage;

  CommentExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
