package com.emmsale.notification.application;

import static com.emmsale.notification.exception.NotificationExceptionType.NOT_FOUND_NOTIFICATION;

import com.emmsale.notification.application.dto.NotificationResponse;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationRepository;
import com.emmsale.notification.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationQueryService {

  private final NotificationRepository notificationRepository;

  public NotificationResponse findNotificationBy(final Long notificationId) {
    final Notification savedNotification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationException(NOT_FOUND_NOTIFICATION));

    return NotificationResponse.from(savedNotification);
  }
}
