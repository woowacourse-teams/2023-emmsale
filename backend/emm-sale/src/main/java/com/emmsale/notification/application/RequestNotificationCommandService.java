package com.emmsale.notification.application;

import static com.emmsale.member.exception.MemberExceptionType.NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER;
import static com.emmsale.notification.exception.NotificationExceptionType.ALREADY_EXIST_NOTIFICATION;
import static com.emmsale.notification.exception.NotificationExceptionType.BAD_REQUEST_MEMBER_ID;
import static com.emmsale.notification.exception.NotificationExceptionType.NOT_FOUND_NOTIFICATION;
import static com.emmsale.notification.exception.NotificationExceptionType.NOT_OWNER;

import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.notification.application.dto.RequestNotificationExistedRequest;
import com.emmsale.notification.application.dto.RequestNotificationModifyRequest;
import com.emmsale.notification.application.dto.RequestNotificationRequest;
import com.emmsale.notification.application.dto.RequestNotificationResponse;
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
  private final MemberRepository memberRepository;
  private final FirebaseCloudMessageClient firebaseCloudMessageClient;

  public RequestNotificationResponse create(
      final RequestNotificationRequest requestNotificationRequest
  ) {
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

  public void modify(
      final RequestNotificationModifyRequest requestNotificationModifyRequest,
      final Long notificationId
  ) {
    final RequestNotification savedRequestNotification = requestNotificationRepository.findById(
            notificationId)
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

  public void delete(final Member member, final Long notificationId) {
    final RequestNotification notification = requestNotificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationException(NOT_FOUND_NOTIFICATION));

    validateNotificationOwner(notification, member);

    requestNotificationRepository.delete(notification);
  }

  private void validateNotificationOwner(
      final RequestNotification notification,
      final Member member
  ) {
    if (notification.isNotOwner(member.getId())) {
      throw new NotificationException(NOT_OWNER);
    }
  }

  public void read(final Long notificationId, final Member member) {
    final RequestNotification notification = requestNotificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationException(NOT_FOUND_NOTIFICATION));

    validateNotificationOwner(notification, member);

    notification.read();
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
