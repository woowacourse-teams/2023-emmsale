package com.emmsale.report.exception;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;

public class ReportException extends BaseException {

  private final ReportExceptionType exceptionType;

  public ReportException(final ReportExceptionType exceptionType) {
    super(exceptionType.errorMessage());
    this.exceptionType = exceptionType;
  }

  @Override
  public BaseExceptionType exceptionType() {
    return exceptionType;
  }
}

