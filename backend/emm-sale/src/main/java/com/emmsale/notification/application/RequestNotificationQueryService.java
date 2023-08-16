package com.emmsale.notification.application;

import static com.emmsale.member.exception.MemberExceptionType.NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER;
import static com.emmsale.notification.exception.NotificationExceptionType.NOT_FOUND_NOTIFICATION;

import com.emmsale.member.domain.Member;
import com.emmsale.member.exception.MemberException;
import com.emmsale.notification.application.dto.RequestNotificationExistedRequest;
import com.emmsale.notification.application.dto.RequestNotificationResponse;
import com.emmsale.notification.domain.RequestNotification;
import com.emmsale.notification.domain.RequestNotificationRepository;
import com.emmsale.notification.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestNotificationQueryService {

  private final RequestNotificationRepository requestNotificationRepository;

  public RequestNotificationResponse findNotificationBy(final Long notificationId) {
    final RequestNotification savedRequestNotification = requestNotificationRepository.findById(
            notificationId)
        .orElseThrow(() -> new NotificationException(NOT_FOUND_NOTIFICATION));

    return RequestNotificationResponse.from(savedRequestNotification);
  }

  public boolean isAlreadyExisted(
      final Member member,
      final RequestNotificationExistedRequest existedRequest
  ) {
    final Long senderId = existedRequest.getSenderId();
    validateSameSender(member, senderId);

    return requestNotificationRepository.existsBySenderIdAndReceiverIdAndEventId(
        senderId,
        existedRequest.getReceiverId(),
        existedRequest.getEventId()
    );
  }

  private void validateSameSender(final Member member, final Long senderId) {
    if (member.isNotMe(senderId)) {
      throw new MemberException(NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER);
    }
  }
}
