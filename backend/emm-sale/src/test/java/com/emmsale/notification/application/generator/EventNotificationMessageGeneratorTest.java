package com.emmsale.notification.application.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventNotificationMessageGeneratorTest {

  private MemberRepository memberRepository;

  private EventNotificationMessageGenerator generator;

  @BeforeEach
  void setUp() {
    memberRepository = mock(MemberRepository.class);
  }

  @Test
  @DisplayName("makeMessage() : FCM 스펙 포맷에 맞게 메시지를 생성할 수 있다.")
  void test_makeMessage() throws Exception {
    //given
    String dateTimeStr = "2023-10-05T15:16:10.402994";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);

    final Notification notification = new Notification(
        NotificationType.EVENT,
        1L,
        2L,
        dateTime,
        "{\"title\":\"행사이름\"}"
    );

    generator = new EventNotificationMessageGenerator(notification);

    when(memberRepository.existsById(any()))
        .thenReturn(true);

    final String expect = "{\"message\":"
        + "{\"data\":"
        + "{\"receiverId\":\"1\","
        + "\"redirectId\":\"2\","
        + "\"notificationType\":\"EVENT\","
        + "\"createdAt\":\"2023-10-05T15:16:10.402994\","
        + "\"title\":\"행사이름\"},"
        + "\"token\":\"targetToken\"},"
        + "\"validate_only\":false}";

    //when
    final String actual = generator.makeMessage(
        "targetToken",
        new ObjectMapper(),
        memberRepository
    );

    //then
    assertEquals(actual, expect);
  }
}
