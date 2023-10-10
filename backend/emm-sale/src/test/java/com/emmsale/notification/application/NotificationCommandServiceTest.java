package com.emmsale.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.NotificationFixture;
import com.emmsale.notification.application.dto.NotificationDeleteRequest;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationRepository;
import com.emmsale.notification.domain.NotificationType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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

  private Notification notification1, notification2;
  private Member member;

  @BeforeEach
  void setUp() {
    member = memberRepository.findById(1L).get();

    notification1 = notificationRepository.save(
        new Notification(
            NotificationType.COMMENT, member.getId(),
            3333L, LocalDateTime.now(),
            NotificationFixture.commentJsonData()
        )
    );

    notification2 = notificationRepository.save(
        new Notification(
            NotificationType.EVENT, 2L,
            3333L, LocalDateTime.now(),
            NotificationFixture.eventJsonData()
        )
    );
  }

  @Test
  @DisplayName("read() : 로그인 한 사용자와 알림 받는 사람이 일치할 때, 해당 알림을 읽음 상태로 변경할 수 있다. ")
  void test_read() throws Exception {
    //when
    notificationCommandService.read(member, notification1.getId());

    //then
    final Notification updateNotification = notificationRepository.findById(notification1.getId())
        .get();

    assertAll(
        () -> assertEquals(updateNotification.getId(), notification1.getId()),
        () -> assertTrue(updateNotification.isRead())
    );
  }

  @Test
  @DisplayName("deleteBatch() : 알림 삭제할 ID로 본인의 알림들만을 삭제할 수 있다.")
  void test_deleteBatch() throws Exception {
    //given
    final List<Long> nonExistedNotificationIds = List.of(4L, 5L, 6L);
    final List<Long> deletedIds = new ArrayList<>();
    deletedIds.addAll(nonExistedNotificationIds);
    deletedIds.add(notification1.getId());
    deletedIds.add(notification2.getId());

    final NotificationDeleteRequest request =
        new NotificationDeleteRequest(deletedIds);

    final List<Notification> expect = List.of(notification2);

    //when
    notificationCommandService.deleteBatch(member, request);

    //then
    final List<Notification> actual = notificationRepository.findAllByIdIn(
        request.getDeleteIds()
    );

    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt")
        .isEqualTo(expect);
  }
}
