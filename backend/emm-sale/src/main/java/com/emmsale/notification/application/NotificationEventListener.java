package com.emmsale.notification.application;

import com.emmsale.event_publisher.UpdateNotificationEvent;
import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.domain.UpdateNotificationRepository;
import com.emmsale.notification.domain.UpdateNotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final UpdateNotificationRepository updateNotificationRepository;
  private final FirebaseCloudMessageClient firebaseCloudMessageClient;


  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  public void createUpdateNotification(final UpdateNotificationEvent updateNotificationEvent) {
    final UpdateNotification updateNotification = new UpdateNotification(
        updateNotificationEvent.getReceiverId(),
        updateNotificationEvent.getRedirectId(),
        UpdateNotificationType.from(updateNotificationEvent.getUpdateNotificationType()),
        updateNotificationEvent.getCreatedAt()
    );

    final UpdateNotification savedNotification =
        updateNotificationRepository.save(updateNotification);

    try {
      firebaseCloudMessageClient.sendMessageTo(savedNotification);
    } catch (Exception e) {
      log.error("파이어베이스 관련 에러, 알림 재요청 필요, {}", e.getMessage(), e);
    }
  }
}
