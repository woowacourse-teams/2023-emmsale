package com.emmsale.notification.application;

import static com.emmsale.member.exception.MemberExceptionType.NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.notification.application.dto.RequestNotificationExistedRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RequestNotificationQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private RequestNotificationQueryService requestNotificationQueryService;
  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("isAlreadyExisted() : 토큰의 주인과 로그인 한 사용자가 다르면 NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER 가 발생한다.")
  void test_isAlreadyExisted() throws Exception {
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
