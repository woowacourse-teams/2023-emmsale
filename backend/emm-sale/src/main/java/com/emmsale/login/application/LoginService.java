package com.emmsale.login.application;

import com.emmsale.login.application.dto.GithubProfileResponse;
import com.emmsale.login.application.dto.TokenResponse;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

  private final GithubClient githubClient;
  private final JwtTokenProvider tokenProvider;

  public LoginService(final GithubClient githubClient, final JwtTokenProvider tokenProvider) {
    this.githubClient = githubClient;
    this.tokenProvider = tokenProvider;
  }

  public TokenResponse createToken(final String code) {
    final String githubAccessToken = githubClient.getAccessTokenFromGithub(code);
    final GithubProfileResponse githubProfileFromGithub = githubClient.getGithubProfileFromGithub(
        githubAccessToken);

    final Long githubId = githubProfileFromGithub.getGithubId();
    //TODO: githubId를 통해 memberId 조회 기능 구현
    final Long memberId = 1L;
    final String accessToken = tokenProvider.createToken(memberId.toString());

    return new TokenResponse(memberId, accessToken);
  }
}
