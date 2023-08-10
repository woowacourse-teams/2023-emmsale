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
  private final LocalDateTime startDate;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime endDate;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime subscriptionStartDate;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime subscriptionEndDate;
  private final List<String> tags;
  private final String status;
  private final String subscriptionStatus;
  private final String imageUrl;
  private final int remainingDays;

  public static List<EventResponse> makeEventResponsesByStatus(LocalDate today, EventStatus status,
      List<Event> events) {
    return events.stream()
        .map(event -> EventResponse.from(today, status, event))
        .collect(toList());
  }

  public static List<EventResponse> mergeEventResponses(LocalDate today,
      final Map<EventStatus, List<Event>> groupByEventStatus) {
    return groupByEventStatus.entrySet().stream()
        .map(entry -> makeEventResponsesByStatus(today, entry.getKey(), entry.getValue()))
        .reduce(new ArrayList<>(), (combinedEvents, eventsToAdd) -> {
          combinedEvents.addAll(eventsToAdd);
          return combinedEvents;
        });
  }

  private static EventResponse from(LocalDate today, EventStatus status, Event event) {
    return
        new EventResponse(event.getId(), event.getName(), event.getStartDate(), event.getEndDate(),
            event.getSubscriptionStartDate(), event.getSubscriptionEndDate(),
            event.getTags()
                .stream()
                .map(tag -> tag.getTag().getName())
                .collect(Collectors.toList()), status.name(),
            event.calculateEventSubscriptionStatus(today).name(), event.getImageUrl(),
            event.calculateRemainingDays(today));
  }

}
