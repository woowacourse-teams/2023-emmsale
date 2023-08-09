package com.emmsale.notification.application.generator;

import static com.emmsale.notification.exception.NotificationExceptionType.CONVERTING_JSON_ERROR;
import static com.emmsale.notification.exception.NotificationExceptionType.NOT_FOUND_OPEN_PROFILE_URL;

import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import com.emmsale.notification.application.dto.RequestNotificationMessage;
import com.emmsale.notification.application.dto.RequestNotificationMessage.Data;
import com.emmsale.notification.application.dto.RequestNotificationMessage.Message;
import com.emmsale.notification.domain.RequestNotification;
import com.emmsale.notification.exception.NotificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RequestNotificationMessageGenerator implements NotificationMessageGenerator {

  private static final String REQUEST_NOTIFICATION_TYPE = "REQUEST";

  private final RequestNotification requestNotification;

  @Override
  public String makeMessage(
      final String targetToken,
      final ObjectMapper objectMapper,
      final MemberRepository memberRepository
  ) {

    final Long senderId = requestNotification.getSenderId();
    final Member sender = memberRepository.findById(senderId)
        .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

    final String openProfileUrl = sender.getOptionalOpenProfileUrl()
        .orElseThrow(() -> new NotificationException(NOT_FOUND_OPEN_PROFILE_URL));

    final Data messageData = new Data(
        sender.getName(), senderId.toString(),
        requestNotification.getReceiverId().toString(), requestNotification.getMessage(),
        openProfileUrl, REQUEST_NOTIFICATION_TYPE, requestNotification.getCreatedAt()
    );

    final RequestNotificationMessage requestNotificationMessage = new RequestNotificationMessage(
        DEFAULT_VALIDATE_ONLY, new Message(messageData, targetToken)
    );

    try {
      return objectMapper.writeValueAsString(requestNotificationMessage);
    } catch (JsonProcessingException e) {
      throw new NotificationException(CONVERTING_JSON_ERROR);
    }
  }
}
