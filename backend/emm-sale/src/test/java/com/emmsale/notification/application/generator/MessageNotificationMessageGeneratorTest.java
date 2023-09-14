package com.emmsale.notification.application.generator;

import static com.emmsale.notification.application.generator.NotificationMessageGenerator.DATE_TIME_FORMATTER;
import static com.emmsale.notification.application.generator.NotificationMessageGenerator.DEFAULT_VALIDATE_ONLY;
import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.event_publisher.MessageNotificationEvent;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.application.dto.MessageNotificationMessage;
import com.emmsale.notification.application.dto.MessageNotificationMessage.Data;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MessageNotificationMessageGeneratorTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("makeMessage(): fcm에 보낼 메시지를 만드는 기능 테스트 ")
  void makeMessage() throws JsonProcessingException {
    //given
    final Member sender = memberRepository.save(new Member(123L, "image", "hong-sile"));
    final Long receiverId = 3L;
    final String roomId = "2131405-abdsL";
    final String token = "token";

    final LocalDateTime messageTime = LocalDateTime.now();
    final String messageContent = "message";

    final MessageNotificationEvent event = new MessageNotificationEvent(
        roomId, messageContent, sender.getId(), receiverId, messageTime
    );

    final MessageNotificationMessageGenerator messageGenerator
        = new MessageNotificationMessageGenerator(event);

    //when
    final String notificationMessage =
        messageGenerator.makeMessage(token, objectMapper, memberRepository);

    //then
    final MessageNotificationMessage actual = objectMapper.readValue(
        notificationMessage, MessageNotificationMessage.class
    );
    final MessageNotificationMessage expected =
        generateExpectedValue(sender, roomId, token, messageTime, messageContent);

    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  private static MessageNotificationMessage generateExpectedValue(final Member sender,
      final String roomId, final String token,
      final LocalDateTime messageTime, final String content) {

    final Data Data = new Data(
        roomId,
        sender.getId().toString(),
        sender.getName(),
        sender.getImageUrl(),
        content,
        messageTime.format(DATE_TIME_FORMATTER)
    );

    return new MessageNotificationMessage(
        DEFAULT_VALIDATE_ONLY,
        new MessageNotificationMessage.Message(
            Data,
            token
        )
    );
  }

}
