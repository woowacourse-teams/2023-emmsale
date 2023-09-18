package com.emmsale.notification.application.generator;

import static com.emmsale.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static com.emmsale.notification.exception.NotificationExceptionType.CONVERTING_JSON_ERROR;

import com.emmsale.event_publisher.MessageNotificationEvent;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.notification.application.dto.MessageNotificationMessage;
import com.emmsale.notification.application.dto.MessageNotificationMessage.Data;
import com.emmsale.notification.exception.NotificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageNotificationMessageGenerator implements NotificationMessageGenerator {

  private static final String MESSAGE_NOTIFICATION_MESSAGE_TYPE = "MESSAGE";

  private final MessageNotificationEvent event;

  @Override
  public String makeMessage(
      final String targetToken,
      final ObjectMapper objectMapper,
      final MemberRepository memberRepository
  ) {
    final Member sender = memberRepository.findById(event.getSenderId())
        .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

    final Data data = new Data(
        MESSAGE_NOTIFICATION_MESSAGE_TYPE,
        event.getRoomId(),
        sender.getId().toString(),
        sender.getName(),
        sender.getImageUrl(),
        event.getContent(),
        event.getMessageCreatedAt().format(DATE_TIME_FORMATTER)
    );

    final MessageNotificationMessage messageNotificationMessage = new MessageNotificationMessage(
        DEFAULT_VALIDATE_ONLY, new MessageNotificationMessage.Message(data, targetToken)
    );

    try {
      return objectMapper.writeValueAsString(messageNotificationMessage);
    } catch (JsonProcessingException e) {
      throw new NotificationException(CONVERTING_JSON_ERROR);
    }
  }
}
