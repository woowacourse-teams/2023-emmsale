package com.emmsale.image.exception;

import com.emmsale.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ImageExceptionType implements BaseExceptionType {
  
  FAIL_UPLOAD_IMAGE(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "이미지 업로드에 실패했습니다."
  ),
  INVALID_FILE_FORMAT(
      HttpStatus.BAD_REQUEST,
      "잘못된 형식의 파일입니다."
  );
  
  private final HttpStatus httpStatus;
  private final String errorMessage;
  
  ImageExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
