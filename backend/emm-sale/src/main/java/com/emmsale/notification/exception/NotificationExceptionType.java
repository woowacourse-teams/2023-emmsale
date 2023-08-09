package com.emmsale.notification.exception;

import com.emmsale.base.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum NotificationExceptionType implements BaseExceptionType {

  BAD_REQUEST_MEMBER_ID(
      HttpStatus.BAD_REQUEST,
      "해당 ID를 가진 멤버는 존재하지 않습니다."
  ),

  NOT_FOUND_NOTIFICATION(
      HttpStatus.NOT_FOUND,
      "알림이 존재하지 않습니다."
  ),

  NOT_FOUND_FCM_TOKEN(
      HttpStatus.NOT_FOUND,
      "해당 사용자의 기기를 구별할 수 있는 FCM 토큰이 존재하지 않습니다."
  ),

  CONVERTING_JSON_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "알림 메시지를 보낼 때 JSON으로 변환하는 과정에서 발생한 에러입니다."
  ),

  GOOGLE_REQUEST_TOKEN_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "구글에 토큰 요청할 때 발생한 에러"
  ),

  NOT_FOUND_OPEN_PROFILE_URL(
      HttpStatus.NOT_FOUND,
      "오픈 카톡 URL이 없으면 같이 가기 요청을 보낼 수 없습니다."
  ),

  FIREBASE_CONNECT_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "파이어베이스 접속 시 발생한 에러"
  ),

  ALREADY_EXIST_NOTIFICATION(
      HttpStatus.BAD_REQUEST,
      "이미 요청된 알림입니다."
  ),
  NOT_OWNER(
      HttpStatus.BAD_REQUEST,
      "알림의 소유자가 아닙니다."
  );

  private final HttpStatus httpStatus;
  private final String errorMessage;

  NotificationExceptionType(final HttpStatus httpStatus, final String errorMessage) {
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
