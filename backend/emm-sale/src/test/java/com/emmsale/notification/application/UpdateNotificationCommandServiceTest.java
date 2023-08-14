package com.emmsale.notification.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.domain.UpdateNotificationRepository;
import com.emmsale.notification.domain.UpdateNotificationType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UpdateNotificationCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private UpdateNotificationCommandService updateNotificationCommandService;
  @Autowired
  private UpdateNotificationRepository updateNotificationRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private MemberRepository memberRepository;

  private UpdateNotification 이벤트_알림;
  private Member member;

  @BeforeEach
  void setUp() {
    final Event event = eventRepository.save(EventFixture.인프콘_2023());
    member = memberRepository.findById(1L).get();

    이벤트_알림 = updateNotificationRepository.save(
        new UpdateNotification(
            member.getId(),
            event.getId(),
            UpdateNotificationType.EVENT,
            LocalDateTime.now()
        )
    );
  }

  @Test
  @DisplayName("read() : 로그인 한 사용자와 알림 받는 사람이 일치할 때, 해당 알림을 읽음 상태로 변경할 수 있다.")
  void test_read() throws Exception {
    //when
    final Long notificationId = 이벤트_알림.getId();
    updateNotificationCommandService.read(member, notificationId);

    //then
    final UpdateNotification updatedNotification =
        updateNotificationRepository.findById(notificationId).get();

    assertAll(
        () -> assertEquals(notificationId, updatedNotification.getId()),
        () -> assertTrue(updatedNotification.isRead())
    );
  }

  @Test
  @DisplayName("read() : 토큰 주인과 로그인 한 사용자가 다르면 읽음 상태를 변경할 수 없다.")
  void test_read_not_matching_token_and_login() throws Exception {
    //given
    final Member otherMember = memberRepository.findById(2L).get();

    //when & then
    assertThatThrownBy(() -> updateNotificationCommandService.read(otherMember, 이벤트_알림.getId()))
        .isInstanceOf(MemberException.class)
        .hasMessage(MemberExceptionType.NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER.errorMessage());
  }
}
