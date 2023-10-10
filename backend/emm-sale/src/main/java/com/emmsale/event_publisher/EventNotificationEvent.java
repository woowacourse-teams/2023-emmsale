package com.emmsale.event_publisher;

import com.emmsale.event.domain.Event;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class EventNotificationEvent extends NotificationEvent {

  private static final String UPDATE_NOTIFICATION_EVENT_TYPE = "EVENT";

  private final String title;

  public EventNotificationEvent(
      final Long receiverId, final Long redirectId,
      final LocalDateTime createdAt, final String notificationType,
      final String title
  ) {
    super(receiverId, redirectId, createdAt, notificationType);
    this.title = title;
  }

  public static EventNotificationEvent of(final Event event, final Long receiverId) {
    return new EventNotificationEvent(
        receiverId,
        event.getId(),
        LocalDateTime.now(),
        UPDATE_NOTIFICATION_EVENT_TYPE,
        event.getName()
    );
  }
}
