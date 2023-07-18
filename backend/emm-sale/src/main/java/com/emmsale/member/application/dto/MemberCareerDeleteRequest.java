package com.emmsale.member.application.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberCareerDeleteRequest {

  private final List<Long> careerIds;

  private MemberCareerDeleteRequest() {
    this(null);
  }
}
