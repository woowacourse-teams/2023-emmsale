package com.emmsale.base;

public abstract class BaseException extends RuntimeException {

  protected BaseException(final String message) {
    super(message);
  }

  public abstract BaseExceptionType exceptionType();
}
