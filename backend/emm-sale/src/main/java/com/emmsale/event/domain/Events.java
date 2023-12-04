package com.emmsale.event.domain;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class Events {

  private final List<Event> events;

  public Events(final List<Event> events) {
    this.events = events;
  }

  public EnumMap<EventStatus, List<Event>> groupByEventStatus(
      final List<EventStatus> eventStatuses,
      final LocalDateTime nowDateTime
  ) {
    return events.stream()
        .filter(isEventContainsStatus(eventStatuses, nowDateTime))
        .sorted(comparing(event -> event.getEventPeriod().getStartDate()))
        .collect(
            groupingBy(event -> event.calculateStatus(nowDateTime),
                () -> new EnumMap<>(EventStatus.class), toList())
        );
  }

  private Predicate<Event> isEventContainsStatus(
      final List<EventStatus> eventStatuses,
      final LocalDateTime nowDateTime
  ) {
    return event -> {
      if (eventStatuses == null || eventStatuses.isEmpty()) {
        return true;
      }
      return eventStatuses.contains(event.calculateStatus(nowDateTime));
    };
  }

  public List<Long> getEventIds() {
    return events.stream()
        .map(Event::getId)
        .collect(Collectors.toUnmodifiableList());
  }
}
