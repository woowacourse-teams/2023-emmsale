package com.emmsale.event.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RecruitmentPostUpdateRequest {

  private final String content;

  private RecruitmentPostUpdateRequest() {
    this(null);
  }
}
