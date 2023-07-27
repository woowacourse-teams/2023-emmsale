package com.emmsale.event.application.dto;

import static java.util.stream.Collectors.toList;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventResponse {

  private final Long id;
  private final String name;
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private final LocalDateTime startDate;
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private final LocalDateTime endDate;
  private final List<String> tags;
  private final String status;

  public static List<EventResponse> makeEventResponsesByStatus(EventStatus status,
      List<Event> events) {
    return events.stream()
        .map(event -> EventResponse.from(status, event))
        .collect(toList());
  }

  public static List<EventResponse> mergeEventResponses(
      final EnumMap<EventStatus, List<Event>> groupByEventStatus) {
    return groupByEventStatus.entrySet().stream()
        .map(entry -> makeEventResponsesByStatus(entry.getKey(), entry.getValue()))
        .reduce(new ArrayList<>(), (combinedEvents, eventsToAdd) -> {
          combinedEvents.addAll(eventsToAdd);
          return combinedEvents;
        });
  }

  private static EventResponse from(EventStatus status, Event event) {
    return
        new EventResponse(event.getId(), event.getName(), event.getStartDate(), event.getEndDate(),
            event.getTags()
                .stream()
                .map(tag -> tag.getTag().getName())
                .collect(Collectors.toList()), status.getValue());
  }

}
