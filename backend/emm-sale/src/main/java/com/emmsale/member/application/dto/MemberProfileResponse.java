package com.emmsale.member.application.dto;

import com.emmsale.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberProfileResponse {

  private static final String GITHUB_URL_PREFIX = "https://github.com/";

  private final Long id;
  private final String name;
  private final String description;
  private final String imageUrl;
  private final String openProfileUrl;
  private final String githubUsername;
  private final String githubUrl;

  public static MemberProfileResponse from(Member member) {
    return new MemberProfileResponse(
        member.getId(),
        member.getName(),
        member.getDescription(),
        member.getImageUrl(),
        member.getOptionalOpenProfileUrl().orElse(""),
        member.getGithubUsername(),
        GITHUB_URL_PREFIX + member.getGithubUsername()
    );
  }
}
