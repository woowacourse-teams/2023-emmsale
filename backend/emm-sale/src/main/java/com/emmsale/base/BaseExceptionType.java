package com.emmsale.base;

import org.springframework.http.HttpStatus;

public interface BaseExceptionType {

  HttpStatus httpStatus();

  String errorMessage();
}
