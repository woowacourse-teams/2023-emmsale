package com.emmsale.notification.application.dto;

import com.emmsale.notification.domain.RequestNotification;
import com.emmsale.notification.domain.RequestNotificationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class RequestNotificationResponse {

  private final Long notificationId;
  private final Long senderId;
  private final Long receiverId;
  private final String message;
  private final Long eventId;
  @JsonProperty(value = "isRead")
  private final boolean isRead;
  private final RequestNotificationStatus status;
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private final LocalDateTime createdAt;

  public static RequestNotificationResponse from(final RequestNotification requestNotification) {
    return new RequestNotificationResponse(
        requestNotification.getId(),
        requestNotification.getSenderId(),
        requestNotification.getReceiverId(),
        requestNotification.getMessage(),
        requestNotification.getEventId(),
        requestNotification.isRead(),
        requestNotification.getStatus(),
        requestNotification.getCreatedAt()
    );
  }

  /**
   * Rest docs 에서 isRead 로 표현하기 위해 사용
   * <br>
   * 해당 메서드가 없으면 read 로 역직렬화 됨
   */
  private boolean getIsRead() {
    return isRead;
  }
}
