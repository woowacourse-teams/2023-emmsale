package com.emmsale.notification.application.generator;

import static com.emmsale.notification.application.dto.UpdateNotificationMessage.Data;
import static com.emmsale.notification.application.dto.UpdateNotificationMessage.Message;
import static com.emmsale.notification.exception.NotificationExceptionType.CONVERTING_JSON_ERROR;

import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.application.dto.UpdateNotificationMessage;
import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.domain.UpdateNotificationType;
import com.emmsale.notification.exception.NotificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateNotificationMessageGenerator implements NotificationMessageGenerator {

  private final UpdateNotification updateNotification;

  @Override
  public String makeMessage(
      final String targetToken,
      final ObjectMapper objectMapper,
      final MemberRepository memberRepository
  ) {

    final Long receiverId = updateNotification.getReceiverId();
    final Long redirectId = updateNotification.getRedirectId();
    final UpdateNotificationType type = updateNotification.getUpdateNotificationType();

    final Data data = new Data(receiverId, redirectId, type);

    final UpdateNotificationMessage updateNotificationMessage =
        new UpdateNotificationMessage(DEFAULT_VALIDATE_ONLY, new Message(data, targetToken));

    try {
      return objectMapper.writeValueAsString(updateNotificationMessage);
    } catch (JsonProcessingException e) {
      throw new NotificationException(CONVERTING_JSON_ERROR);
    }
  }
}
