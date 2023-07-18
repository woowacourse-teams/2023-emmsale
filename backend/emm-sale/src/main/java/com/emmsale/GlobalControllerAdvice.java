package com.emmsale;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ExceptionResponse> handleException(final BaseException e) {
    final BaseExceptionType type = e.exceptionType();
    log.warn("[WARN] MESSAGE: {}", type.errorMessage());
    return new ResponseEntity<>(ExceptionResponse.from(e), type.httpStatus());
  }

  static class ExceptionResponse {

    private final String message;

    private ExceptionResponse(final String message) {
      this.message = message;
    }

    private static ExceptionResponse from(final BaseException e) {
      final BaseExceptionType type = e.exceptionType();
      return new ExceptionResponse(type.errorMessage());
    }

    public String getMessage() {
      return message;
    }
  }
}
