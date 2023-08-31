package com.emmsale.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.emmsale.notification.application.dto.UpdateNotificationDeleteRequest;
import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.domain.UpdateNotificationRepository;
import com.emmsale.notification.domain.UpdateNotificationType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  private UpdateNotification notification1, notification2, notification3;

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

    final Long receiverId = member.getId();

    notification1 = new UpdateNotification(
        receiverId, 2L,
        UpdateNotificationType.COMMENT, LocalDateTime.now()
    );
    notification2 = new UpdateNotification(
        receiverId, 3L,
        UpdateNotificationType.EVENT, LocalDateTime.now()
    );
    notification3 = new UpdateNotification(
        2L, 4L,
        UpdateNotificationType.COMMENT, LocalDateTime.now()
    );

    updateNotificationRepository.saveAll(List.of(notification1, notification2, notification3));
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

  @Test
  @DisplayName("deleteBatch() : 댓글 & 행사 알림을 삭제할 ID로 본인의 알림들만을 삭제할 수 있다.")
  void test_deleteBatch() throws Exception {
    //given
    final List<Long> nonExistedNotificationIds = List.of(4L, 5L, 6L);
    final List<Long> deletedIds = new ArrayList<>();
    deletedIds.addAll(nonExistedNotificationIds);
    deletedIds.addAll(List.of(
        notification1.getId(),
        notification2.getId(),
        notification3.getId()
    ));

    final UpdateNotificationDeleteRequest request =
        new UpdateNotificationDeleteRequest(deletedIds);

    final List<UpdateNotification> expected = List.of(notification3);

    //when
    updateNotificationCommandService.deleteBatch(member, request);

    //then
    final List<UpdateNotification> actual =
        updateNotificationRepository.findAllByIdIn(request.getDeleteIds());

    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .ignoringFields("createdAt")
        .isEqualTo(expected);
  }
}
