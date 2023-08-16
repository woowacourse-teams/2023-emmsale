package com.emmsale.member.application.dto;

import com.emmsale.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberProfileResponse {

  private final Long id;
  private final String name;
  private final String description;
  private final String imageUrl;
  private final String openProfileUrl;
  private final Long githubId;

  public static MemberProfileResponse from(Member member) {
    return new MemberProfileResponse(
        member.getId(),
        member.getName(),
        member.getDescription(),
        member.getImageUrl(),
        member.getOptionalOpenProfileUrl().orElse(""),
        member.getGithubId()
    );
  }
}
