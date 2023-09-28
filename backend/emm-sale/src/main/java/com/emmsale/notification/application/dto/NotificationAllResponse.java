package com.emmsale.notification.application.dto;

import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NotificationAllResponse {

  private final Long notificationId;
  private final NotificationType type;
  private final String notificationInformation;
  @JsonProperty(value = "isRead")
  private final boolean isRead;

  public static NotificationAllResponse from(final Notification notification) {
    return new NotificationAllResponse(
        notification.getId(),
        notification.getType(),
        notification.getJsonData(),
        notification.isRead()
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
