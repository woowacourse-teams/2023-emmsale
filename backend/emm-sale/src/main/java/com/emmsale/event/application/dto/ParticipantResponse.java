package com.emmsale.event.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ParticipantResponse {

  private final Long id;
  private final Long memberId;
  private final String name;
  private final String imageUrl;
  private final String description;
}
