package com.emmsale.event_publisher;

import com.emmsale.comment.event.UpdateNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public void publish(final UpdateNotificationEvent event) {
    applicationEventPublisher.publishEvent(event);
  }
}
