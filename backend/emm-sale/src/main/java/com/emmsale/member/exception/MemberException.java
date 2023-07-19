package com.emmsale.member.exception;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;

public class MemberException extends BaseException {

  private final MemberExceptionType exceptionType;

  public MemberException(final MemberExceptionType exceptionType) {
    super(exceptionType.errorMessage());
    this.exceptionType = exceptionType;
  }

  @Override
  public BaseExceptionType exceptionType() {
    return exceptionType;
  }
}
