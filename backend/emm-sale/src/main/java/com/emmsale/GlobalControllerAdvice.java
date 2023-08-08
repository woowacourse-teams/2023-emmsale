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

    if (type.httpStatus().is5xxServerError()) {
      log.error("[ERROR] MESSAGE : {}, 연락주세요 : {}", type.errorMessage(), e);
      return new ResponseEntity<>(ExceptionResponse.from(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

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

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponse> handleException(final Exception exception) {
    log.error("[ERROR] 로그 캡처와 함께 서버 개발자에게 연락주세요 : ", exception);
    return new ResponseEntity<>(
        ExceptionResponse.from(exception),
        HttpStatus.INTERNAL_SERVER_ERROR
    );
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

    private static ExceptionResponse from(final Exception exception) {
      return new ExceptionResponse(exception.getMessage());
    }

    public String getMessage() {
      return message;
    }
  }
}
