package com.emmsale.event.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EventParticipateRequest {

  private final Long memberId;

  private EventParticipateRequest() {
    this(null);
  }
}
