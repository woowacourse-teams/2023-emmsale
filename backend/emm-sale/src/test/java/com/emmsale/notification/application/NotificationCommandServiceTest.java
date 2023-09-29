package com.emmsale.notification.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationRepository;
import com.emmsale.notification.domain.NotificationType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NotificationCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private NotificationCommandService notificationCommandService;
  @Autowired
  private NotificationRepository notificationRepository;
  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("read() : 로그인 한 사용자와 알림 받는 사람이 일치할 때, 해당 알림을 읽음 상태로 변경할 수 있다. ")
  void test_read() throws Exception {
    //given
    final Member member = memberRepository.findById(1L).get();

    final String commentJsonData1 = "{"
        + "\"content\":\"content\","
        + "\"writer\":\"writer\","
        + "\"writerImageUrl\":\"imageUrl\""
        + "}";

    final Notification notification = notificationRepository.save(
        new Notification(
            NotificationType.COMMENT, member.getId(),
            3333L, LocalDateTime.now(),
            commentJsonData1
        )
    );

    //when
    notificationCommandService.read(member, notification.getId());

    //then
    final Notification updateNotification = notificationRepository.findById(notification.getId()).get();

    assertAll(
        () -> assertEquals(updateNotification.getId(), notification.getId()),
        () -> assertTrue(updateNotification.isRead())
    );
  }
}
