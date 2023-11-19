package com.emmsale.admin.login.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.emmsale.admin.login.application.dto.AdminLoginRequest;
import com.emmsale.admin.login.application.dto.AdminTokenResponse;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.login.exception.LoginException;
import com.emmsale.login.exception.LoginExceptionType;
import com.emmsale.login.utils.JwtTokenProvider;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AdminLoginServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private AdminLoginService adminLoginService;
  @MockBean
  private JwtTokenProvider tokenProvider;
  @Value("${data.admin_login.id}")
  private String adminId;
  @Value("${data.admin_login.password}")
  private String adminPassword;
  @Value("${data.admin_login.member_id}")
  private Long adminMemberId;

  @Test
  @DisplayName("관리자 아이디, 패스워드로 사용자를 조회하여 토큰을 생성한다.")
  void createAdminToken_success() {
    // given
    final Long memberId = adminMemberId;
    final AdminLoginRequest request = new AdminLoginRequest(adminId, adminPassword);
    final String expectAccessToken = "expect_access_token";
    given(tokenProvider.createToken(String.valueOf(memberId))).willReturn(expectAccessToken);

    // when
    final AdminTokenResponse actualToken = adminLoginService.createAdminToken(request);

    // then
    assertEquals(expectAccessToken, actualToken.getAccessToken());
  }

  @Test
  @DisplayName("관리자 정보 요청이 null이면 예외를 반환한다.")
  void createAdminToken_fail_not_found_request() {
    // given
    final LoginExceptionType expectExceptionType = LoginExceptionType.NOT_FOUND_ADMIN_LOGIN_INFORMATION;

    // when
    final ThrowingCallable actual = () -> adminLoginService.createAdminToken(null);

    // then
    Assertions.assertThatThrownBy(actual)
        .isInstanceOf(LoginException.class)
        .hasMessage(expectExceptionType.errorMessage());
  }

  @Test
  @DisplayName("관리자 아이디가 올바르지 않으면 예외를 반환한다.")
  void createAdminToken_fail_invalid_id() {
    // given
    final AdminLoginRequest request = new AdminLoginRequest("invalid", adminPassword);
    final LoginExceptionType expectExceptionType = LoginExceptionType.INVALID_ADMIN_LOGIN_INFORMATION;

    // when
    final ThrowingCallable actual = () -> adminLoginService.createAdminToken(
        request);

    // then
    Assertions.assertThatThrownBy(actual)
        .isInstanceOf(LoginException.class)
        .hasMessage(expectExceptionType.errorMessage());
  }

  @Test
  @DisplayName("관리자 패스워드가 올바르지 않으면 예외를 반환한다.")
  void createAdminToken_fail_invalid_password() {
    // given
    final AdminLoginRequest request = new AdminLoginRequest(adminId, "invalid");
    final LoginExceptionType expectExceptionType = LoginExceptionType.INVALID_ADMIN_LOGIN_INFORMATION;

    // when
    final ThrowingCallable actual = () -> adminLoginService.createAdminToken(
        request);

    // then
    Assertions.assertThatThrownBy(actual)
        .isInstanceOf(LoginException.class)
        .hasMessage(expectExceptionType.errorMessage());
  }

}