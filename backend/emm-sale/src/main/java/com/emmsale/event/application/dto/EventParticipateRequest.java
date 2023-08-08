package com.emmsale.event.application.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EventParticipateRequest {

  private final Long memberId;
  @NotBlank
  @Size(max = 255)
  private final String content;
}
