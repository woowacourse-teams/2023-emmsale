package com.emmsale.event_publisher;

import com.emmsale.event.domain.Event;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EventNotificationEvent {

  private static final String UPDATE_NOTIFICATION_EVENT_TYPE = "event";

  private final Long receiverId;
  private final Long redirectId;
  private final String notificationType;
  private final LocalDateTime createdAt;
  private final String title;

  public static EventNotificationEvent of(final Event event, final Long receiverId) {
    return new EventNotificationEvent(
        receiverId,
        event.getId(),
        UPDATE_NOTIFICATION_EVENT_TYPE,
        LocalDateTime.now(),
        event.getName()
    );
  }
}
