package com.emmsale.login.application;

import com.emmsale.login.application.dto.GithubProfileResponse;
import com.emmsale.login.application.dto.MemberQueryResponse;
import com.emmsale.login.application.dto.TokenResponse;
import com.emmsale.login.utils.GithubClient;
import com.emmsale.login.utils.JwtTokenProvider;
import com.emmsale.member.application.MemberQueryService;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

  private final GithubClient githubClient;
  private final JwtTokenProvider tokenProvider;
  private final MemberQueryService memberQueryService;

  public LoginService(
      final GithubClient githubClient,
      final JwtTokenProvider tokenProvider,
      final MemberQueryService memberQueryService
  ) {
    this.githubClient = githubClient;
    this.tokenProvider = tokenProvider;
    this.memberQueryService = memberQueryService;
  }

  public TokenResponse createToken(final String code) {
    final String githubAccessToken = githubClient.getAccessTokenFromGithub(code);
    final GithubProfileResponse githubProfileFromGithub = githubClient.getGithubProfileFromGithub(
        githubAccessToken);

    final MemberQueryResponse memberQueryResponse = memberQueryService.findOrCreateMember(
        githubProfileFromGithub
    );
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
