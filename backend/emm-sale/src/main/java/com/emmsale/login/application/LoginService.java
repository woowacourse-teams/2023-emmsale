package com.emmsale.login.application;

import com.emmsale.login.application.dto.GithubProfileResponse;
import com.emmsale.login.application.dto.MemberQueryResponse;
import com.emmsale.login.application.dto.TokenResponse;
import com.emmsale.login.domain.OAuthProfileResponse;
import com.emmsale.login.domain.OAuthProviderComposite;
import com.emmsale.login.domain.OAuthProviderType;
import com.emmsale.login.utils.GithubClient;
import com.emmsale.login.utils.JwtTokenProvider;
import com.emmsale.member.application.MemberQueryService;
import com.emmsale.member.domain.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

  private final OAuthProviderComposite oAuthProviderComposite;
  private final GithubClient githubClient;
  private final JwtTokenProvider tokenProvider;
  private final MemberQueryService memberQueryService;
  private final MemberRepository memberRepository;

  public LoginService(
      final OAuthProviderComposite oAuthProviderComposite,
      final GithubClient githubClient,
      final JwtTokenProvider tokenProvider,
      final MemberQueryService memberQueryService,
      final MemberRepository memberRepository
  ) {
    this.oAuthProviderComposite = oAuthProviderComposite;
    this.githubClient = githubClient;
    this.tokenProvider = tokenProvider;
    this.memberQueryService = memberQueryService;
    this.memberRepository = memberRepository;
  }

  public TokenResponse createTokenByGithub(final String code) {
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
        memberQueryResponse.isOnboarded(),
        accessToken
    );
  }

  public TokenResponse createToken(final String code, final String oauthTypeName) {
    final OAuthProviderType oAuthProviderType = OAuthProviderType.from(oauthTypeName);
    final OAuthProfileResponse profile = oAuthProviderComposite
        .getProfileResponse(code, oAuthProviderType);

    final MemberQueryResponse memberQueryResponse = memberQueryService.findOrCreateMemberByOAuthProfileAndType(
        profile, oAuthProviderType
    );

    final String accessToken = tokenProvider.createToken(
        String.valueOf(memberQueryResponse.getMemberId())
    );

    return new TokenResponse(
        memberQueryResponse.getMemberId(),
        memberQueryResponse.isOnboarded(),
        accessToken
    );
  }
}
