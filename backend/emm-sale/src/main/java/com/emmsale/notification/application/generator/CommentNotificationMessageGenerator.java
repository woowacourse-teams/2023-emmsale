package com.emmsale.notification.application.generator;

import static com.emmsale.notification.exception.NotificationExceptionType.BAD_REQUEST_MEMBER_ID;
import static com.emmsale.notification.exception.NotificationExceptionType.CONVERTING_JSON_ERROR;

import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.application.dto.CommentNotificationMessage;
import com.emmsale.notification.application.dto.CommentNotificationMessage.Data;
import com.emmsale.notification.application.dto.CommentNotificationMessage.Message;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.exception.NotificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentNotificationMessageGenerator implements NotificationMessageGenerator {

  private final Notification notification;

  @Override
  public String makeMessage(
      final String targetToken,
      final ObjectMapper objectMapper,
      final MemberRepository memberRepository
  ) {

    final String jsonData = notification.getJsonData();

    try {

      final Data data = objectMapper.readValue(jsonData, Data.class);

      validateIsExistedReceiver(memberRepository, Long.valueOf(data.getReceiverId()));

      final CommentNotificationMessage message = new CommentNotificationMessage(
          DEFAULT_VALIDATE_ONLY, new Message(data, targetToken)
      );

      return objectMapper.writeValueAsString(message);

    } catch (JsonProcessingException e) {
      throw new NotificationException(CONVERTING_JSON_ERROR);
    }
  }

  private void validateIsExistedReceiver(final MemberRepository memberRepository,
      final Long receiverId) {
    if (!memberRepository.existsById(receiverId)) {
      throw new NotificationException(BAD_REQUEST_MEMBER_ID);
    }
  }
}
