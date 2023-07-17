package com.emmsale.member.application.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberCareerInitialRequest {

  private final String name;
  private final List<Long> careerIds;
}
