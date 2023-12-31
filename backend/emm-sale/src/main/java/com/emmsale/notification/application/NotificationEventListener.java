package com.emmsale.notification.application;

import com.emmsale.event_publisher.MessageNotificationEvent;
import com.emmsale.event_publisher.NotificationEvent;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationRepository;
import com.emmsale.notification.domain.NotificationType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

  private final FirebaseCloudMessageClient firebaseCloudMessageClient;
  private final NotificationRepository notificationRepository;
  private final ObjectMapper objectMapper;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  public void createNotification(final NotificationEvent notificationEvent) {
    try {
      final String jsonData = objectMapper.writeValueAsString(notificationEvent);

      final Notification notification = notificationRepository.save(
          new Notification(
              NotificationType.valueOf(notificationEvent.getNotificationType()),
              notificationEvent.getReceiverId(),
              notificationEvent.getRedirectId(),
              notificationEvent.getCreatedAt(),
              jsonData
          )
      );

      firebaseCloudMessageClient.sendMessageTo(
          notification,
          notification.getReceiverId()
      );

    } catch (JsonProcessingException e) {
      log.error("json 에러");
    } catch (Exception e) {
      log.error("파이어베이스 관련 에러, 알림 재요청 필요, {}", e.getMessage(), e);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  public void createMessageNotification(final MessageNotificationEvent messageNotificationEvent) {
    try {
      firebaseCloudMessageClient.sendMessageTo(messageNotificationEvent);
    } catch (Exception e) {
      log.error("파이어베이스 관련 에러, 알림 재요청 필요, {}", e.getMessage(), e);
    }
  }
}
