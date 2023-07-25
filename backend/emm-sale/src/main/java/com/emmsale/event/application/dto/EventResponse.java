package com.emmsale.event.application.dto;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventResponse {

  private final Long id;
  private final String name;
  private final LocalDateTime startDate;
  private final LocalDateTime endDate;
  private final List<String> tags;
  private final String status;

  public static EventResponse from(EventStatus status, Event event) {
    return
        new EventResponse(event.getId(), event.getName(), event.getStartDate(), event.getEndDate(),
            event.getTags()
                .stream()
                .map(tag -> tag.getTag().getName())
                .collect(Collectors.toList()), status.getValue());
  }
}
