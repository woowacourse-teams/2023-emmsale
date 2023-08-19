package com.emmsale.notification.application;

import com.emmsale.event_publisher.UpdateNotificationEvent;
import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.domain.UpdateNotificationRepository;
import com.emmsale.notification.domain.UpdateNotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final UpdateNotificationRepository updateNotificationRepository;
  private final FirebaseCloudMessageClient firebaseCloudMessageClient;

  @EventListener
  public void createUpdateNotification(final UpdateNotificationEvent updateNotificationEvent) {
    final UpdateNotification updateNotification = new UpdateNotification(
        updateNotificationEvent.getReceiverId(),
        updateNotificationEvent.getRedirectId(),
        UpdateNotificationType.from(updateNotificationEvent.getUpdateNotificationType()),
        updateNotificationEvent.getCreatedAt()
    );

    final UpdateNotification savedNotification =
        updateNotificationRepository.save(updateNotification);

    firebaseCloudMessageClient.sendMessageTo(savedNotification);
  }
}
