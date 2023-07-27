package com.emmsale.event.application;

import static com.emmsale.event.exception.EventExceptionType.EVENT_NOT_FOUND_EXCEPTION;
import static com.emmsale.event.exception.EventExceptionType.INVALID_MONTH;
import static com.emmsale.event.exception.EventExceptionType.INVALID_STATUS;
import static com.emmsale.event.exception.EventExceptionType.INVALID_YEAR;
import static com.emmsale.tag.exception.TagExceptionType.NOT_FOUND_TAG;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventTag;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.EventTagRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import com.emmsale.tag.exception.TagException;
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

  private static final int MIN_MONTH = 1;
  private static final int MAX_MONTH = 12;
  private static final int MIN_YEAR = 2015;

  private final EventRepository eventRepository;
  private final EventTagRepository eventTagRepository;
  private final TagRepository tagRepository;

  @Transactional(readOnly = true)
  public EventDetailResponse findEvent(final Long id) {
    final Event event = eventRepository.findById(id)
        .orElseThrow(() -> new EventException(EVENT_NOT_FOUND_EXCEPTION));
    return EventDetailResponse.from(event);
  }

  @Transactional(readOnly = true)
  public List<EventResponse> findEvents(final LocalDate nowDate, final int year, final int month,
      final String tagName, final String statusName) {
    validateYearAndMonth(year, month);
    List<Event> events = filterEventsByTag(tagName);

    final EnumMap<EventStatus, List<Event>> sortAndGroupByStatus
        = groupByEventStatus(nowDate, events, year, month);

    return filterEventResponsesByStatus(statusName, sortAndGroupByStatus);
  }

  private void validateYearAndMonth(final int year, final int month) {
    if (year < MIN_YEAR) {
      throw new EventException(INVALID_YEAR);
    }
    if (month < MIN_MONTH || month > MAX_MONTH) {
      throw new EventException(INVALID_MONTH);
    }
  }

  private List<Event> filterEventsByTag(final String tagName) {
    if (isExistTagName(tagName)) {
      Tag tag = tagRepository.findByName(tagName)
          .orElseThrow(() -> new TagException(NOT_FOUND_TAG));

      return eventTagRepository.findEventTagsByTag(tag)
          .stream()
          .map(EventTag::getEvent)
          .collect(toList());
    }
    return eventRepository.findAll();
  }

  private boolean isExistTagName(final String tagName) {
    return tagName != null;
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

  private List<EventResponse> filterEventResponsesByStatus(final String statusName,
      final EnumMap<EventStatus, List<Event>> sortAndGroupByEventStatus) {
    if (isaBoolean(statusName)) {
      EventStatus status = findEventStatusByValue(statusName);
      return makeEventResponsesByStatus(status, sortAndGroupByEventStatus.get(status));
    }
    return mergeEventResponses(sortAndGroupByEventStatus);
  }

  private boolean isaBoolean(final String statusName) {
    return statusName != null;
  }

  private EventStatus findEventStatusByValue(final String value) {
    return Arrays.stream(EventStatus.values())
        .filter(status -> status.isSameValue(value))
        .findFirst()
        .orElseThrow(() -> new EventException(INVALID_STATUS));
  }

  private List<EventResponse> makeEventResponsesByStatus(EventStatus status,
      List<Event> events) {
    return events.stream()
        .map(event -> EventResponse.from(status, event))
        .collect(toList());
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
