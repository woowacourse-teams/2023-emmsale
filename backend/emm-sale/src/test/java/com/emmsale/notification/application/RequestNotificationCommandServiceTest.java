package com.emmsale.notification.application;

import static com.emmsale.notification.exception.NotificationExceptionType.BAD_REQUEST_MEMBER_ID;
import static com.emmsale.notification.exception.NotificationExceptionType.NOT_OWNER;
import static com.emmsale.notification.exception.NotificationExceptionType.NO_CONTENT_BLOCKED_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import com.emmsale.block.domain.Block;
import com.emmsale.block.domain.BlockRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.application.dto.RequestNotificationModifyRequest;
import com.emmsale.notification.application.dto.RequestNotificationRequest;
import com.emmsale.notification.application.dto.RequestNotificationResponse;
import com.emmsale.notification.domain.RequestNotification;
import com.emmsale.notification.domain.RequestNotificationRepository;
import com.emmsale.notification.domain.RequestNotificationStatus;
import com.emmsale.notification.exception.NotificationException;
import com.emmsale.notification.exception.NotificationExceptionType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

class RequestNotificationCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private RequestNotificationCommandService requestNotificationCommandService;
  @Autowired
  private RequestNotificationRepository requestNotificationRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private BlockRepository blockRepository;
  private RequestNotificationCommandService mockingRequestNotificationCommandService;
  private FirebaseCloudMessageClient firebaseCloudMessageClient;

  @BeforeEach
  void setUp() {
    firebaseCloudMessageClient = mock(FirebaseCloudMessageClient.class);

    mockingRequestNotificationCommandService = new RequestNotificationCommandService(
        requestNotificationRepository,
        memberRepository,
        firebaseCloudMessageClient,
        blockRepository
    );
  }

  @Test
  @DisplayName("create() : 알림을 새로 생성할 수 있다.")
  void test_create() throws Exception {
    //given
    final long senderId = 1L;
    final long receiverId = 2L;
    final long eventId = 3L;
    final String message = "알림 메시지야";
    final long notificationId = 1L;

    final RequestNotificationRequest request = new RequestNotificationRequest(
        senderId,
        receiverId,
        message,
        eventId
    );

    final RequestNotificationResponse expected = new RequestNotificationResponse(
        notificationId, senderId, receiverId, message, eventId
    );

    doNothing().when(firebaseCloudMessageClient).sendMessageTo((RequestNotification) any());

    //when
    final RequestNotificationResponse actual = mockingRequestNotificationCommandService.create(
        request);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(expected);
  }

  @ParameterizedTest
  @DisplayName("createToken() : sender나 receiver가 한명이라도 존재하지 않는다면 BAD_REQUEST_MEMBER_ID 를 반환할 수 있다.")
  @CsvSource({
      "1,3",
      "3,1",
      "4,5"
  })
  void test_createToken_badRequestMemberId(final Long senderId, final Long receiverId)
      throws Exception {
    //given
    final long eventId = 3L;
    final String message = "알림 메시지야";

    final RequestNotificationRequest request = new RequestNotificationRequest(
        senderId,
        receiverId,
        message,
        eventId
    );

    //when
    assertThatThrownBy(() -> requestNotificationCommandService.create(request))
        .isInstanceOf(NotificationException.class)
        .hasMessage(BAD_REQUEST_MEMBER_ID.errorMessage());

    //then
  }

  @Test
  @DisplayName("modify() : 알림의 상태를 변경할 수 있다.")
  void test_modify() throws Exception {
    //given
    final long senderId = 1L;
    final long receiverId = 2L;
    final long eventId = 3L;
    final String message = "알림 메시지야";

    final RequestNotificationModifyRequest request =
        new RequestNotificationModifyRequest(RequestNotificationStatus.IN_PROGRESS);
    final RequestNotification savedRequestNotification =
        requestNotificationRepository.save(
            new RequestNotification(senderId, receiverId, eventId, message));

    //when
    requestNotificationCommandService.modify(request, savedRequestNotification.getId());

    final RequestNotification updatedRequestNotification =
        requestNotificationRepository.findById(savedRequestNotification.getId()).get();

    //then
    assertEquals(request.getUpdatedStatus(), updatedRequestNotification.getStatus());
  }

  @Test
  @DisplayName("Member가 받은 모든 알림 목록을 조회한다.")
  void test_findAllNotifications() {
    //given
    final Member sender = memberRepository.findById(1L).get();
    final Member receiver = memberRepository.findById(2L).get();

    final String message1 = "message123";
    final String message2 = "message321";

    requestNotificationRepository.save(
        new RequestNotification(sender.getId(), receiver.getId(), 123L, message1)
    );
    requestNotificationRepository.save(
        new RequestNotification(sender.getId(), receiver.getId(), 321L, message2)
    );

    //when
    final List<RequestNotificationResponse> notifications = requestNotificationCommandService.findAllNotifications(
        receiver);

    //then
    assertThat(notifications).extracting("message", String.class)
        .containsExactly(message1, message2);
  }

  @Test
  @DisplayName("알림을 삭제한다.")
  void test_delete() {
    //given
    final Member sender = memberRepository.findById(1L).get();
    final Member receiver = memberRepository.findById(2L).get();

    final String message = "message";
    final long eventId = 123L;

    final RequestNotification notification = requestNotificationRepository.save(
        new RequestNotification(sender.getId(), receiver.getId(), eventId, message)
    );
    final Long notificationId = notification.getId();

    //when
    requestNotificationCommandService.delete(receiver, notificationId);

    //then
    assertFalse(requestNotificationRepository.findById(notificationId).isPresent());
  }

  @Test
  @DisplayName("알림의 소유자가 아닐 경우 NOT_OWNER 타입의 NotificationException이 발생한다.")
  void test_deleteByNotOwner() {
    //given
    final Member sender = memberRepository.findById(1L).get();
    final Member receiver = memberRepository.findById(2L).get();

    final String message = "message";
    final long eventId = 123L;

    final RequestNotification notification = requestNotificationRepository.save(
        new RequestNotification(sender.getId(), receiver.getId(), eventId, message)
    );
    final Long notificationId = notification.getId();

    final NotificationExceptionType expectExceptionType = NOT_OWNER;

    //when
    final NotificationException actualException = assertThrowsExactly(
        NotificationException.class,
        () -> requestNotificationCommandService.delete(sender, notificationId)
    );

    //then
    assertEquals(expectExceptionType, actualException.exceptionType());
  }

  @Test
  @DisplayName("차단된 사용자에게 알림을 보낼 경우 NO_CONTENT_BLOCKED_MEMBER 타입의 Exception이 발생한다.")
  void createWithBlockedSender() {
    //given
    final long senderId = 1L;
    final long receiverId = 2L;
    final long eventId = 3L;
    final String message = "알림 메시지야";

    blockRepository.save(new Block(receiverId, senderId));

    final RequestNotificationRequest request = new RequestNotificationRequest(
        senderId,
        receiverId,
        message,
        eventId
    );

    //when
    final NotificationException actualException = assertThrowsExactly(NotificationException.class,
        () -> requestNotificationCommandService.create(request));

    //then
    assertEquals(NO_CONTENT_BLOCKED_MEMBER, actualException.exceptionType());
  }
}
