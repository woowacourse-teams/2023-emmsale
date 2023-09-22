package com.emmsale.image.exception;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;

public class ImageException extends BaseException {
  
  private final ImageExceptionType exceptionType;
  
  public ImageException(final ImageExceptionType exceptionType) {
    super(exceptionType.errorMessage());
    this.exceptionType = exceptionType;
  }
  
  @Override
  public BaseExceptionType exceptionType() {
    return exceptionType;
  }
  
}
