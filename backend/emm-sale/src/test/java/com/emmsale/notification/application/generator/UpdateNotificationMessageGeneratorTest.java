package com.emmsale.notification.application.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.application.dto.UpdateNotificationMessage;
import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.domain.UpdateNotificationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UpdateNotificationMessageGeneratorTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private ObjectMapper objectMapper;
  private UpdateNotificationMessageGenerator messageGenerator;
  private Member member;
  private UpdateNotificationType type;

  @BeforeEach
  void setUp() {
    member = memberRepository.findById(1L).get();
    type = UpdateNotificationType.COMMENT;
    final long redirectId = 2L;

    final UpdateNotification updateNotification = new UpdateNotification(
        member.getId(),
        redirectId,
        type,
        LocalDateTime.now()
    );

    messageGenerator = new UpdateNotificationMessageGenerator(updateNotification);
  }

  @Test
  @DisplayName("makeMessage() : Message 포맷에 맞게 Message를 생성할 수 있다.")
  void test_makeMessage() throws Exception {
    //given
    final String targetToken = "token";

    //when
    final String expected = messageGenerator.makeMessage(
        targetToken,
        objectMapper,
        memberRepository
    );

    //then
    final UpdateNotificationMessage expectedMessage = objectMapper.readValue(
        expected, UpdateNotificationMessage.class
    );

    assertEquals(type.toString(), expectedMessage.getMessage().getData().getNotificationType());
  }
}
