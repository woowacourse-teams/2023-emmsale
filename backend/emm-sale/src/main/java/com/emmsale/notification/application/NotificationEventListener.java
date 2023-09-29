package com.emmsale.notification.application;

import com.emmsale.event_publisher.CommentNotificationEvent;
import com.emmsale.event_publisher.EventNotificationEvent;
import com.emmsale.event_publisher.MessageNotificationEvent;
import com.emmsale.event_publisher.UpdateNotificationEvent;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationRepository;
import com.emmsale.notification.domain.NotificationType;
import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.domain.UpdateNotificationRepository;
import com.emmsale.notification.domain.UpdateNotificationType;
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

  private final UpdateNotificationRepository updateNotificationRepository;
  private final FirebaseCloudMessageClient firebaseCloudMessageClient;
  private final NotificationRepository notificationRepository;
  private final ObjectMapper objectMapper;

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

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  public void createCommentNotification(final CommentNotificationEvent commentNotificationEvent) {
    try {
      final String jsonData = objectMapper.writeValueAsString(commentNotificationEvent);

      final Notification notification = notificationRepository.save(
          new Notification(
              NotificationType.COMMENT, commentNotificationEvent.getReceiverId(),
              commentNotificationEvent.getRedirectId(), commentNotificationEvent.getCreatedAt(),
              jsonData
          )
      );

      firebaseCloudMessageClient.sendMessageTo(
          notification,
          commentNotificationEvent.getReceiverId()
      );

    } catch (JsonProcessingException e) {
      log.error("json 에러");
    } catch (Exception e) {
      log.error("파이어베이스 관련 에러, 알림 재요청 필요, {}", e.getMessage(), e);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener
  public void createEventNotification(final EventNotificationEvent eventNotificationEvent) {
    try {
      final String jsonData = objectMapper.writeValueAsString(eventNotificationEvent);

      final Notification notification = notificationRepository.save(
          new Notification(
              NotificationType.EVENT, eventNotificationEvent.getReceiverId(),
              eventNotificationEvent.getRedirectId(), eventNotificationEvent.getCreatedAt(),
              jsonData
          )
      );

      firebaseCloudMessageClient.sendMessageTo(
          notification,
          eventNotificationEvent.getReceiverId()
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
