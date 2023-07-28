package com.emmsale.member.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberProfileResponse {

  private final Long id;
  private final String name;
  private final String description;
  private final String imageUrl;

}
