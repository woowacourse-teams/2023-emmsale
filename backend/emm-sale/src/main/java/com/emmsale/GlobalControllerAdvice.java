package com.emmsale;

import com.emmsale.base.BaseException;
import com.emmsale.base.BaseExceptionType;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionResponse> handleValidationException(
      final MethodArgumentNotValidException e) {
    final String message = e.getBindingResult().getAllErrors().stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining("\n"));
    log.warn("[WARN] MESSAGE: {}", message);
    return new ResponseEntity<>(new ExceptionResponse(message), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DateTimeParseException.class)
  public ResponseEntity<ExceptionResponse> handleDateTimeParseException(
      final DateTimeParseException e) {
    final String message = "DateTime 입력 형식이 올바르지 않습니다.";
    log.warn("[WARN] MESSAGE: " + message);
    return new ResponseEntity<>(new ExceptionResponse(message), HttpStatus.BAD_REQUEST);
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
