package com.emmsale.login.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.BDDMockito.given;

import com.emmsale.base.BaseExceptionType;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.login.application.dto.GithubProfileResponse;
import com.emmsale.login.application.dto.TokenResponse;
import com.emmsale.login.exception.LoginException;
import com.emmsale.login.exception.LoginExceptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LoginServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private LoginService loginService;

  @Test
  @DisplayName("깃허브 id로부터 사용자를 조회하여 토큰을 생성한다.")
  void getAccessTokenFromGithub() {
    // given
    final String memberId = "1";
    final String validCode = "valid_code";
    final String validAccessToken = "valid_access_token";
    final GithubProfileResponse githubProfile = new GithubProfileResponse(memberId, "name",
        "username", "imageUrl");

    final String expectAccessToken = "expect_access_token";
    final TokenResponse expectTokenResponse = new TokenResponse(1L, false, expectAccessToken);

    given(githubClient.getAccessTokenFromGithub(validCode)).willReturn(validAccessToken);
    given(githubClient.getGithubProfileFromGithub(validAccessToken)).willReturn(githubProfile);

    // when
    final TokenResponse actualToken = loginService.createTokenByGithub(validCode);

    // then
    assertEquals(expectTokenResponse.getMemberId(), actualToken.getMemberId());
  }

  @Test
  @DisplayName("깃허브 Access Token이 유효하지 않을 경우 404 NOT_FOUND가 발생한다.")
  void getAccessTokenFromGithubWithInvalidAccessToken() {
    // given
    final String validCode = "valid_code";
    final String invalidAccessToken = "invalid_access_token";
    final LoginExceptionType expectExceptionType = LoginExceptionType.INVALID_GITHUB_ACCESS_TOKEN;

    given(githubClient.getAccessTokenFromGithub(validCode)).willReturn(invalidAccessToken);
    given(githubClient.getGithubProfileFromGithub(invalidAccessToken)).willThrow(
        new LoginException(expectExceptionType)
    );

    // when
    final BaseExceptionType actualExceptionType = assertThrowsExactly(
        LoginException.class,
        () -> loginService.createTokenByGithub(validCode)
    ).exceptionType();

    // then
    assertAll(
        () -> assertEquals(actualExceptionType.httpStatus(), expectExceptionType.httpStatus()),
        () -> assertEquals(actualExceptionType.errorMessage(), expectExceptionType.errorMessage())
    );
  }

  @Test
  @DisplayName("깃허브 code가 유효하지 않을 경우 400 BAD_REQUEST가 발생한다.")
  void getAccessTokenFromGithubWithInvalidCode() {
    // given
    final String invalidCode = "invalid_code";
    final LoginExceptionType expectExceptionType = LoginExceptionType.NOT_FOUND_GITHUB_ACCESS_TOKEN;

    given(githubClient.getAccessTokenFromGithub(invalidCode)).willThrow(
        new LoginException(expectExceptionType)
    );

    // when
    final BaseExceptionType actualExceptionType = assertThrowsExactly(
        LoginException.class,
        () -> loginService.createTokenByGithub(invalidCode)
    ).exceptionType();

    // then
    assertAll(
        () -> assertEquals(actualExceptionType.httpStatus(), expectExceptionType.httpStatus()),
        () -> assertEquals(actualExceptionType.errorMessage(), expectExceptionType.errorMessage())
    );
  }

  @Test
  @DisplayName("OAuth type과 auth code를 받아서, 실제 member의 JWT를 생성해준다.")
  void createToken() {
    // given
    final String memberId = "1";
    final String validCode = "valid_code";
    final String validAccessToken = "valid_access_token";
    final String oauthType = "github";
    final GithubProfileResponse githubProfile
        = new GithubProfileResponse(memberId, "name", "username", "imageUrl");

    final String expectAccessToken = "expect_access_token";
    final TokenResponse expectTokenResponse = new TokenResponse(1L, false, expectAccessToken);

    given(githubClient.getAccessTokenFromGithub(validCode)).willReturn(validAccessToken);
    given(githubClient.getGithubProfileFromGithub(validAccessToken)).willReturn(githubProfile);

    // when
    final TokenResponse actualToken = loginService.createToken(validCode, oauthType);

    // then
    assertEquals(expectTokenResponse.getMemberId(), actualToken.getMemberId());
  }
}
