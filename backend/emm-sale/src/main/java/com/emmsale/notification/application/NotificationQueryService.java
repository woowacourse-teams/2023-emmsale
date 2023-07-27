package com.emmsale.notification.application;

import com.emmsale.notification.application.dto.NotificationResponse;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationQueryService {

  private final NotificationRepository notificationRepository;

  public NotificationResponse findNotificationBy(final Long notificationId) {
    //TODO : Notification Exception 이 정해지면 수정
    final Notification savedNotification = notificationRepository.findById(notificationId)
        .orElseThrow(EntityNotFoundException::new);

    return NotificationResponse.from(savedNotification);
  }
}
