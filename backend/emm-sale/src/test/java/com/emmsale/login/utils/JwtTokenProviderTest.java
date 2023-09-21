package com.emmsale.login.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import com.emmsale.base.BaseExceptionType;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.login.exception.LoginException;
import com.emmsale.login.exception.LoginExceptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class JwtTokenProviderTest extends ServiceIntegrationTestHelper {

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Test
  @DisplayName("토큰이 유효할 경우 토큰으로부터 사용자의 아이디를 추출하여 반환한다.")
  void createTokenAndExtractSubjectTest() {
    // given
    final String memberId = "1";
    final String token = jwtTokenProvider.createToken(memberId);

    // when
    final String subject = jwtTokenProvider.extractSubject(token);

    // then
    assertThat(subject).isEqualTo(memberId);
  }

  @Test
  @DisplayName("토큰이 유효하지 않을 경우 400, BAD_REQUEST가 발생한다.")
  void createTokenAndExtractSubjectWithExpiredTokenTest() {
    //given
    final String token = "invalid_token";

    final LoginExceptionType expectExceptionType = LoginExceptionType.INVALID_ACCESS_TOKEN;

    //when
    final BaseExceptionType actualExceptionType = assertThrowsExactly(
        LoginException.class,
        () -> jwtTokenProvider.extractSubject(token)
    ).exceptionType();

    //then
    assertThat(actualExceptionType).isEqualTo(expectExceptionType);
  }

  @Test
  @DisplayName("토큰을 사용한다.")
  void Test() {
    final JwtTokenProvider jwtTokenProvider1 = new JwtTokenProvider();

    final String token = jwtTokenProvider1.createToken("1");
    System.out.println(token);
  }
}
