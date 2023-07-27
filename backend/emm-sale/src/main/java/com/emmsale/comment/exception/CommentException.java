package com.emmsale.comment.exception;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;

public class CommentException extends BaseException {

  private final CommentExceptionType exceptionType;

  public CommentException(final CommentExceptionType exceptionType) {
    super(exceptionType.errorMessage());
    this.exceptionType = exceptionType;
  }

  @Override
  public BaseExceptionType exceptionType() {
    return exceptionType;
  }
}
