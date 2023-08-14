package com.emmsale.notification.application;

import static com.emmsale.member.exception.MemberExceptionType.NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER;
import static com.emmsale.notification.exception.NotificationExceptionType.NOT_FOUND_NOTIFICATION;

import com.emmsale.member.domain.Member;
import com.emmsale.member.exception.MemberException;
import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.domain.UpdateNotificationRepository;
import com.emmsale.notification.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateNotificationCommandService {

  private final UpdateNotificationRepository updateNotificationRepository;

  public void read(final Member authMember, final Long notificationId) {
    final UpdateNotification savedNotification =
        updateNotificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationException(NOT_FOUND_NOTIFICATION));

    validateSameMember(authMember, savedNotification.getReceiverId());

    savedNotification.read();
  }

  private void validateSameMember(final Member authMember, final Long loginMemberId) {
    if (authMember.isNotMe(loginMemberId)) {
      throw new MemberException(NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER);
    }
  }
}
