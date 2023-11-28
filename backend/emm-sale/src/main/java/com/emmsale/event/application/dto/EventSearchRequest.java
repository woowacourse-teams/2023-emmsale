package com.emmsale.event.application.dto;

import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventType;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventSearchRequest {

  private final EventType category;
  private final List<String> tags;
  private final List<EventStatus> statuses;
  private final String keyword;
}
