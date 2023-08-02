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

  public static MemberProfileResponse from(Member member) {
    return new MemberProfileResponse(member.getId(), member.getName(),
        member.getDescription(), member.getImageUrl());
  }
}
