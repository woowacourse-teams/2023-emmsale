package com.emmsale.event.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RecruitmentPostDeleteRequest {

  private final Long memberId;

  private RecruitmentPostDeleteRequest() {
    this(null);
  }
}
