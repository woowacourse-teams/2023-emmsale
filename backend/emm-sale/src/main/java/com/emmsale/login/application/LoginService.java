package com.emmsale.login.application;

import com.emmsale.login.application.dto.GithubProfileResponse;
import com.emmsale.login.application.dto.TokenResponse;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

  private final GithubClient githubClient;

  public LoginService(final GithubClient githubClient) {
    this.githubClient = githubClient;
  }

  public TokenResponse createToken(final String code) {
    final String githubAccessToken = githubClient.getAccessTokenFromGithub(code);
    final GithubProfileResponse githubProfileFromGithub = githubClient.getGithubProfileFromGithub(
        githubAccessToken);

    //TODO: memberId 조회
    final Long githubId = githubProfileFromGithub.getGithubId();
    final Long memberId = 1L;
    //TODO: accessToken 생성
    final String accessToken = "";

    return new TokenResponse(memberId, accessToken);
  }
}
