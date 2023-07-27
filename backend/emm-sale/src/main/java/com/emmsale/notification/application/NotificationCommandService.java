package com.emmsale.notification.application;

import com.emmsale.notification.application.dto.NotificationRequest;
import com.emmsale.notification.application.dto.NotificationResponse;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandService {

  private final NotificationRepository notificationRepository;

  public NotificationResponse create(final NotificationRequest notificationRequest) {
    final Notification savedNotification = notificationRepository.save(
        new Notification(
            notificationRequest.getSenderId(),
            notificationRequest.getReceiverId(),
            notificationRequest.getEventId(),
            notificationRequest.getMessage()
        ));


    //FCM에 notification 보내기
    return NotificationResponse.from(savedNotification);
  }
}
