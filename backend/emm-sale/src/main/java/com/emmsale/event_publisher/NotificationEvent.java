package com.emmsale.event_publisher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class NotificationEvent {

  @JsonIgnore
  private final Long receiverId;
  @JsonIgnore
  private final Long redirectId;
  @JsonIgnore
  private final LocalDateTime createdAt;
  @JsonIgnore
  private final String notificationType;
}
