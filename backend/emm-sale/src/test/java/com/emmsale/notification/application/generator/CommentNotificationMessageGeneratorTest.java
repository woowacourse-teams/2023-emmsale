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

class CommentNotificationMessageGeneratorTest {

  private MemberRepository memberRepository;

  private CommentNotificationMessageGenerator generator;

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
        NotificationType.COMMENT,
        1L,
        2L,
        dateTime,
        "{\"content\":\"내용2\",\"writer\":\"작성자1\",\"writerImageUrl\":\"image\"}"
    );

    generator = new CommentNotificationMessageGenerator(notification);

    when(memberRepository.existsById(any()))
        .thenReturn(true);

    final String expect = "{\"message\":"
        + "{\"data\":"
        + "{\"receiverId\":\"1\","
        + "\"redirectId\":\"2\","
        + "\"notificationType\":\"COMMENT\","
        + "\"createdAt\":\"2023-10-05T15:16:10.402994\","
        + "\"content\":\"내용2\","
        + "\"writer\":\"작성자1\","
        + "\"writerImageUrl\":\"image\"},"
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
