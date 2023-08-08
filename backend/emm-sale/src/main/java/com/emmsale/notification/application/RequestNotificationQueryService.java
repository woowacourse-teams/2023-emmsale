package com.emmsale.notification.application;

import static com.emmsale.notification.exception.NotificationExceptionType.NOT_FOUND_NOTIFICATION;

import com.emmsale.notification.application.dto.RequestNotificationResponse;
import com.emmsale.notification.domain.RequestNotification;
import com.emmsale.notification.domain.RequestNotificationRepository;
import com.emmsale.notification.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestNotificationQueryService {

  private final RequestNotificationRepository requestNotificationRepository;

  public RequestNotificationResponse findNotificationBy(final Long notificationId) {
    final RequestNotification savedRequestNotification = requestNotificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationException(NOT_FOUND_NOTIFICATION));

    return RequestNotificationResponse.from(savedRequestNotification);
  }
}
