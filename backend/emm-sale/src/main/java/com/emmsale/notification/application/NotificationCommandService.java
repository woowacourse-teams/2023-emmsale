package com.emmsale.notification.application;

import com.emmsale.notification.application.dto.FcmTokenRequest;
import com.emmsale.notification.application.dto.NotificationRequest;
import com.emmsale.notification.application.dto.NotificationResponse;
import com.emmsale.notification.domain.FcmToken;
import com.emmsale.notification.domain.FcmTokenRepository;
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
  private final FcmTokenRepository fcmTokenRepository;

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

  public void createToken(final FcmTokenRequest fcmTokenRequest) {
    final Long memberId = fcmTokenRequest.getMemberId();
    final String token = fcmTokenRequest.getToken();

    fcmTokenRepository.findByMemberId(memberId)
        .ifPresentOrElse(
            it -> it.update(token),
            () -> fcmTokenRepository.save(new FcmToken(token, memberId))
        );
  }
}
