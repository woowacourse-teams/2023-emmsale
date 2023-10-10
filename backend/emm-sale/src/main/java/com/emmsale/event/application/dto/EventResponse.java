package com.emmsale.event.application.dto;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class EventResponse {

  public static final String DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss";

  private final Long id;
  private final String name;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime eventStartDate;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime eventEndDate;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime applyStartDate;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime applyEndDate;
  private final List<String> tags;
  private final String thumbnailUrl;
  private final String eventMode;
  private final String paymentType;

  public static List<EventResponse> makeEventResponsesByStatus(final List<Event> events,
      final Map<Long, String> imageUrlPerEventId) {
    return events.stream()
        .map(event -> EventResponse.from(event, imageUrlPerEventId.get(event.getId())))
        .collect(Collectors.toList());
  }

  public static List<EventResponse> mergeEventResponses(
      final Map<EventStatus, List<Event>> groupByEventStatus,
      final Map<Long, String> imageUrlPerEventId
  ) {
    return groupByEventStatus.values().stream()
        .map(events -> makeEventResponsesByStatus(events, imageUrlPerEventId))
        .reduce(new ArrayList<>(), (combinedEvents, eventsToAdd) -> {
          combinedEvents.addAll(eventsToAdd);
          return combinedEvents;
        });
  }

  private static EventResponse from(final Event event, final String thumbnailUrl) {
    return new EventResponse(
        event.getId(),
        event.getName(),
        event.getEventPeriod().getStartDate(),
        event.getEventPeriod().getEndDate(),
        event.getEventPeriod().getApplyStartDate(),
        event.getEventPeriod().getApplyEndDate(),
        event.extractTags(),
        thumbnailUrl,
        event.getEventMode().getValue(),
        event.getPaymentType().getValue()
    );
  }
}
