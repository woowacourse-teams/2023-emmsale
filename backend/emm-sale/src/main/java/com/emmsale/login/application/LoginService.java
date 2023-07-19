package com.emmsale.login.application;

import com.emmsale.login.application.dto.GithubProfileResponse;
import com.emmsale.login.application.dto.MemberQueryResponse;
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
    //TODO: githubId를 통해 memberId 조회 (처음 가입한 사용자인 경우 회원가입 후 memberId 반환)
    final MemberQueryResponse memberQueryResponse = new MemberQueryResponse(1L, false);
    final String accessToken = tokenProvider.createToken(
        String.valueOf(memberQueryResponse.getMemberId())
    );

    return new TokenResponse(
        memberQueryResponse.getMemberId(),
        memberQueryResponse.isNewMember(),
        accessToken
    );
  }
}
