package com.emmsale.event.application;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventRepository;
import com.emmsale.event.domain.EventStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

  private final EventRepository eventRepository;

  public List<EventResponse> findEvents(final LocalDate nowDate, final int year, final int month,
      final List<String> tags, final String status) {

    List<Event> events = eventRepository.findAll();

    final EnumMap<EventStatus, List<Event>> sortAndGroupByEventStatus
        = groupByEventStatus(nowDate, events, year, month);

    return makeEventResponses(sortAndGroupByEventStatus);
  }

  private EnumMap<EventStatus, List<Event>> groupByEventStatus(final LocalDate nowDate,
      final List<Event> events, final int year, final int month) {
    return events.stream()
        .filter(event -> isOverlapToMonth(year, month,
            event.getStartDate().toLocalDate(), event.getEndDate().toLocalDate())
        )
        .sorted(comparing(Event::getStartDate))
        .collect(
            groupingBy(event -> extractEventStatus(nowDate, event.getStartDate().toLocalDate(),
                event.getEndDate().toLocalDate()), () -> new EnumMap<>(EventStatus.class), toList())
        );
  }

  private boolean isOverlapToMonth(final int year, final int month,
      final LocalDate eventStart, final LocalDate eventEnd) {
    LocalDate monthStart = LocalDate.of(year, month, 1);
    LocalDate monthEnd = LocalDate.of(year, month, monthStart.lengthOfMonth());

    return (isBeforeOrEquals(eventStart, monthEnd) && isBeforeOrEquals(monthStart, eventEnd))
        || (isBeforeOrEquals(monthStart, eventStart) && isBeforeOrEquals(eventStart, monthEnd))
        || (isBeforeOrEquals(monthStart, eventEnd) && isBeforeOrEquals(eventEnd, monthEnd));
  }

  private boolean isBeforeOrEquals(LocalDate criteria, LocalDate comparison) {
    return criteria.isBefore(comparison) || criteria.isEqual(comparison);
  }

  private EventStatus extractEventStatus(LocalDate now, LocalDate startDate, LocalDate endDate) {
    if (now.isBefore(startDate)) {
      return EventStatus.UPCOMING;
    }
    if (now.isAfter(endDate)) {
      return EventStatus.ENDED;
    }
    return EventStatus.IN_PROGRESS;
  }

  private List<EventResponse> makeEventResponses(
      final EnumMap<EventStatus, List<Event>> groupByEventStatus) {
    List<EventResponse> responses = new ArrayList<>();

    for (final Entry<EventStatus, List<Event>> entry : groupByEventStatus.entrySet()) {
      final List<EventResponse> statusResponses = entry.getValue()
          .stream()
          .map(event -> EventResponse.from(entry.getKey(), event))
          .collect(toList());
      responses.addAll(statusResponses);
    }
    return responses;
  }
}
