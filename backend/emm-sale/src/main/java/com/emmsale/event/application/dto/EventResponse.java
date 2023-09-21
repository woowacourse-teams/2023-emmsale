package com.emmsale.event.application.dto;

import static java.util.stream.Collectors.toList;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
  private final LocalDateTime startDate;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime endDate;
  private final List<String> tags;
  private final String status;
  private final String applyStatus;
  private final String imageUrl;
  private final int remainingDays;
  private final int applyRemainingDays;
  private final String eventMode;
  private final String paymentType;
  private final String organization;

  public static List<EventResponse> makeEventResponsesByStatus(
      final LocalDate today,
      final EventStatus status,
      final List<Event> events
  ) {
    return events.stream()
        .map(event -> EventResponse.from(today, status, event))
        .collect(toList());
  }

  public static List<EventResponse> mergeEventResponses(
      final LocalDate today,
      final Map<EventStatus, List<Event>> groupByEventStatus
  ) {
    return groupByEventStatus.entrySet().stream()
        .map(entry -> makeEventResponsesByStatus(today, entry.getKey(), entry.getValue()))
        .reduce(new ArrayList<>(), (combinedEvents, eventsToAdd) -> {
          combinedEvents.addAll(eventsToAdd);
          return combinedEvents;
        });
  }

  private static EventResponse from(
      final LocalDate today,
      final EventStatus status,
      final Event event
  ) {
    return new EventResponse(
        event.getId(),
        event.getName(),
        event.getEventPeriod().getStartDate(),
        event.getEventPeriod().getEndDate(),
        event.extractTags(),
        status.name(),
        event.getEventPeriod().calculateEventApplyStatus(today).name(),
        event.getImageUrl(),
        event.getEventPeriod().calculateRemainingDays(today),
        event.getEventPeriod().calculateApplyRemainingDays(today),
        event.getEventMode().getValue(),
        event.getPaymentType().getValue(),
        event.getOrganization()
    );
  }
}
