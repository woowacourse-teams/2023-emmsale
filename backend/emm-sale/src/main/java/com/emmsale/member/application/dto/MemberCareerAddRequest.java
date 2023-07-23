package com.emmsale.member.application.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberCareerAddRequest {

  private final List<Long> careerIds;

  private MemberCareerAddRequest() {
    this(null);
  }
}
