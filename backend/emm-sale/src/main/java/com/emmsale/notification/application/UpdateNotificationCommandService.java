package com.emmsale.notification.application;

import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.domain.UpdateNotificationRepository;
import com.emmsale.comment.event.UpdateNotificationEvent;
import com.emmsale.notification.domain.UpdateNotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateNotificationCommandService {

  private final UpdateNotificationRepository updateNotificationRepository;
  private final FirebaseCloudMessageClient firebaseCloudMessageClient;

  @EventListener
  public void createUpdateNotification(final UpdateNotificationEvent updateNotificationEvent) {
    final UpdateNotification updateNotification = new UpdateNotification(
        updateNotificationEvent.getReceiverId(),
        updateNotificationEvent.getRedirectId(),
        UpdateNotificationType.from(updateNotificationEvent.getUpdateNotificationType())
    );

    final UpdateNotification savedNotification =
        updateNotificationRepository.save(updateNotification);

    firebaseCloudMessageClient.sendMessageTo(savedNotification);
  }
}
