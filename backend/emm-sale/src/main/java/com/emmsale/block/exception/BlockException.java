package com.emmsale.block.exception;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;

public class BlockException extends BaseException {

  private final BlockExceptionType exceptionType;

  public BlockException(final BlockExceptionType blockExceptionType) {
    super(blockExceptionType.errorMessage());
    this.exceptionType = blockExceptionType;
  }

  @Override
  public BaseExceptionType exceptionType() {
    return exceptionType;
  }
}
