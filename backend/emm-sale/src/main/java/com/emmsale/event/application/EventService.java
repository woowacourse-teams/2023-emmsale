package com.emmsale.event.application;

import static com.emmsale.event.exception.EventExceptionType.INVALID_STATUS;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventRepository;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.exception.EventException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

  private final EventRepository eventRepository;


  private static List<EventResponse> makeEventResponsesByStatus(EventStatus status,
      List<Event> events) {
    return events.stream()
        .map(event -> EventResponse.from(status, event))
        .collect(toList());
  }

  public List<EventResponse> findEvents(final LocalDate nowDate, final int year, final int month,
      final String tag, final String status) {

    List<Event> events = eventRepository.findAll();

    final EnumMap<EventStatus, List<Event>> sortAndGroupByEventStatus
        = groupByEventStatus(nowDate, events, year, month);

    if (status != null) {
      EventStatus eventStatus = findEventStatusByValue(status);
      return makeEventResponsesByStatus(eventStatus, sortAndGroupByEventStatus.get(eventStatus));
    }
    return mergeEventResponses(sortAndGroupByEventStatus);
  }

  private EventStatus findEventStatusByValue(final String value) {
    return Arrays.stream(EventStatus.values())
        .filter(status -> status.isSameValue(value))
        .findFirst()
        .orElseThrow(() -> new EventException(INVALID_STATUS));
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

  private List<EventResponse> mergeEventResponses(
      final EnumMap<EventStatus, List<Event>> groupByEventStatus) {
    return groupByEventStatus.entrySet().stream()
        .map(entry -> makeEventResponsesByStatus(entry.getKey(), entry.getValue()))
        .reduce(new ArrayList<>(), (combinedEvents, eventsToAdd) -> {
          combinedEvents.addAll(eventsToAdd);
          return combinedEvents;
        });
  }
}
