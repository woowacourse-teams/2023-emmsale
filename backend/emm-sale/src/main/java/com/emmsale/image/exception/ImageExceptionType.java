package com.emmsale.image.exception;

import com.emmsale.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ImageExceptionType implements BaseExceptionType {
  
  INVALID_FILE_FORMAT(
      HttpStatus.BAD_REQUEST,
      "잘못된 형식의 파일입니다."
  ),
  FAIL_S3_UPLOAD_IMAGE(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "이미지를 S3에 업로드하지 못했습니다."
  ),
  FAIL_DB_UPLOAD_IMAGE(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "이미지를 DB에 업로드하지 못했습니다."
  ),
  OVER_MAX_IMAGE_COUNT(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "컨텐츠의 최대 게시 가능 이미지 수를 초과했습니다."
  ),
  FAIL_S3_DELETE_IMAGE(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "이미지를 S3에서 삭제하지 못했습니다."
  ),
  FAIL_DB_DELETE_IMAGE(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "이미지를 DB서 삭제하지 못했습니다."
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
