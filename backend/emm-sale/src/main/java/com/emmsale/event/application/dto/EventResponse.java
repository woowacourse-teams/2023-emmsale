package com.emmsale.event.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EventResponse {

  private final Long id;
  private final String name;
  private final String informationUrl;
  private final String startDate;
  private final String endDate;
  private final String location;
}
