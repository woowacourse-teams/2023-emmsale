package com.emmsale.member.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberActivityResponse {

  private final Long id;
  private final String name;
}
