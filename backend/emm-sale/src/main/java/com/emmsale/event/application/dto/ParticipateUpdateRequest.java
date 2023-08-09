package com.emmsale.event.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ParticipateUpdateRequest {

  private final String content;

  private ParticipateUpdateRequest() {
    this(null);
  }
}
