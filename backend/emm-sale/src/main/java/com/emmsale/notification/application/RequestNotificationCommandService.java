package com.emmsale.notification.application;

import static com.emmsale.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static com.emmsale.notification.exception.NotificationExceptionType.ALREADY_EXIST_NOTIFICATION;
import static com.emmsale.notification.exception.NotificationExceptionType.BAD_REQUEST_MEMBER_ID;
import static com.emmsale.notification.exception.NotificationExceptionType.NOT_FOUND_NOTIFICATION;

import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.notification.application.dto.FcmTokenRequest;
import com.emmsale.notification.application.dto.RequestNotificationModifyRequest;
import com.emmsale.notification.application.dto.RequestNotificationRequest;
import com.emmsale.notification.application.dto.RequestNotificationResponse;
import com.emmsale.notification.domain.FcmToken;
import com.emmsale.notification.domain.FcmTokenRepository;
import com.emmsale.notification.domain.RequestNotification;
import com.emmsale.notification.domain.RequestNotificationRepository;
import com.emmsale.notification.exception.NotificationException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestNotificationCommandService {

  private final RequestNotificationRepository requestNotificationRepository;
  private final FcmTokenRepository fcmTokenRepository;
  private final MemberRepository memberRepository;
  private final FirebaseCloudMessageClient firebaseCloudMessageClient;

  public RequestNotificationResponse create(final RequestNotificationRequest requestNotificationRequest) {
    final Long senderId = requestNotificationRequest.getSenderId();
    final Long receiverId = requestNotificationRequest.getReceiverId();
    final Long eventId = requestNotificationRequest.getEventId();

    validateAlreadyExistedNotification(senderId, receiverId, eventId);
    validateExistedSenderOrReceiver(List.of(senderId, receiverId));

    final RequestNotification savedRequestNotification = requestNotificationRepository.save(
        new RequestNotification(
            senderId,
            receiverId,
            eventId,
            requestNotificationRequest.getMessage()
        ));

    firebaseCloudMessageClient.sendMessageTo(savedRequestNotification);

    return RequestNotificationResponse.from(savedRequestNotification);
  }

  private void validateAlreadyExistedNotification(
      final Long senderId,
      final Long receiverId,
      final Long eventId
  ) {
    if (requestNotificationRepository.existsBySenderIdAndReceiverIdAndEventId(
        senderId, receiverId, eventId
    )) {
      throw new NotificationException(ALREADY_EXIST_NOTIFICATION);
    }
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
      final RequestNotificationModifyRequest requestNotificationModifyRequest,
      final Long notificationId
  ) {
    final RequestNotification savedRequestNotification = requestNotificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationException(NOT_FOUND_NOTIFICATION));

    savedRequestNotification.modifyStatus(requestNotificationModifyRequest.getUpdatedStatus());
  }

  public List<RequestNotificationResponse> findAllNotifications(final Member member) {
    final List<RequestNotification> requestNotifications = requestNotificationRepository.findAllByReceiverId(
        member.getId());

    return requestNotifications.stream()
        .map(RequestNotificationResponse::from)
        .collect(Collectors.toList());
  }
}
