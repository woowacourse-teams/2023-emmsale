package com.emmsale.member.application.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberCareerRequest {

  private final List<Long> careerIds;

  private MemberCareerRequest() {
    this(null);
  }
}
