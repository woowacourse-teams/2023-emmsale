package com.emmsale.event.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EventCancelParticipateRequest {

  private final Long memberId;

  private EventCancelParticipateRequest() {
    this(null);
  }
}
