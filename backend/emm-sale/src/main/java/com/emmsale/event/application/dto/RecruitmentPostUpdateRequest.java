package com.emmsale.event.application.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RecruitmentPostUpdateRequest {

  @NotBlank
  private final String content;

  private RecruitmentPostUpdateRequest() {
    this(null);
  }
}
