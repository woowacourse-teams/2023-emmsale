package com.emmsale.notification.application;

import static com.emmsale.member.exception.MemberExceptionType.NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.notification.application.dto.RequestNotificationExistedRequest;
import com.emmsale.notification.domain.RequestNotification;
import com.emmsale.notification.domain.RequestNotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RequestNotificationQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private RequestNotificationQueryService requestNotificationQueryService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private RequestNotificationRepository requestNotificationRepository;

  @Test
  @DisplayName("isAlreadyExisted() : 행사에서 이미 알림을 보낸 적이 있다면 true를 반환할 수 있다.")
  void test_isAlreadyExisted() throws Exception {
    //given
    final long senderId = 1L;
    final Member member = memberRepository.findById(senderId).get();
    final long eventId = 3L;
    final long receiverId = 2L;

    final RequestNotification notification =
        new RequestNotification(senderId, receiverId, eventId, "알림");
    requestNotificationRepository.save(notification);

    final RequestNotificationExistedRequest request =
        new RequestNotificationExistedRequest(
            receiverId,
            member.getId(),
            eventId
        );

    //when
    final boolean actual = requestNotificationQueryService
        .isAlreadyExisted(member, request);

    //then
    assertTrue(actual);
  }

  @Test
  @DisplayName("isAlreadyExisted() : 토큰의 주인과 로그인 한 사용자가 다르면 NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER 가 발생한다.")
  void test_isAlreadyExisted_not_matching_token_and_login_member() throws Exception {
    //given
    final long senderId = 1L;
    final Member member = memberRepository.findById(senderId).get();
    final long eventId = 3L;
    final long receiverId = 2L;
    final RequestNotificationExistedRequest request =
        new RequestNotificationExistedRequest(
            receiverId,
            4L,
            eventId
        );

    //when & then
    assertThatThrownBy(() -> requestNotificationQueryService.isAlreadyExisted(member, request))
        .isInstanceOf(MemberException.class)
        .hasMessage(NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER.errorMessage());
  }
}
