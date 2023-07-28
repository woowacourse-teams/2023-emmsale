package com.emmsale.notification.application;

import static com.emmsale.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static com.emmsale.notification.exception.NotificationExceptionType.BAD_REQUEST_MEMBER_ID;
import static com.emmsale.notification.exception.NotificationExceptionType.NOT_FOUND_NOTIFICATION;

import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.notification.application.dto.FcmTokenRequest;
import com.emmsale.notification.application.dto.NotificationModifyRequest;
import com.emmsale.notification.application.dto.NotificationRequest;
import com.emmsale.notification.application.dto.NotificationResponse;
import com.emmsale.notification.domain.FcmToken;
import com.emmsale.notification.domain.FcmTokenRepository;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationRepository;
import com.emmsale.notification.exception.NotificationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandService {

  private final NotificationRepository notificationRepository;
  private final FcmTokenRepository fcmTokenRepository;
  private final MemberRepository memberRepository;

  public NotificationResponse create(final NotificationRequest notificationRequest) {
    final Long senderId = notificationRequest.getSenderId();
    final Long receiverId = notificationRequest.getReceiverId();

    final List<Long> memberIds = List.of(senderId, receiverId);

    if (isNotExistedSenderOrReceiver(memberIds)) {
      throw new NotificationException(BAD_REQUEST_MEMBER_ID);
    }

    validateExistedSenderOrReceiver(memberIds);

    final Notification savedNotification = notificationRepository.save(
        new Notification(
            senderId,
            receiverId,
            notificationRequest.getEventId(),
            notificationRequest.getMessage()
        ));

    //FCM에 notification 보내기
    return NotificationResponse.from(savedNotification);
  }

  private void validateExistedSenderOrReceiver(final List<Long> memberIds) {
    if (isNotExistedSenderOrReceiver(memberIds)) {
      throw new NotificationException(BAD_REQUEST_MEMBER_ID);
    }
  }

  private boolean isNotExistedSenderOrReceiver(final List<Long> memberIds) {
    return memberIds.size() != memberRepository.countMembersById(memberIds);
  }

  public void registerFcmToken(final FcmTokenRequest fcmTokenRequest) {
    final Long memberId = fcmTokenRequest.getMemberId();
    final String token = fcmTokenRequest.getToken();

    validateExistedMember(memberId);

    fcmTokenRepository.findByMemberId(memberId)
        .ifPresentOrElse(
            it -> it.update(token),
            () -> fcmTokenRepository.save(new FcmToken(token, memberId))
        );
  }

  private void validateExistedMember(final Long memberId) {
    if (isNotExisted(memberId)) {
      throw new MemberException(NOT_FOUND_MEMBER);
    }
  }

  private boolean isNotExisted(final Long memberId) {
    return !memberRepository.existsById(memberId);
  }

  public void modify(
      final NotificationModifyRequest notificationModifyRequest,
      final Long notificationId
  ) {
    final Notification savedNotification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationException(NOT_FOUND_NOTIFICATION));

    savedNotification.modifyStatus(notificationModifyRequest.getUpdatedStatus());
  }
}
