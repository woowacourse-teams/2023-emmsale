package com.emmsale.event.application;

import static com.emmsale.event.exception.EventExceptionType.INVALID_MONTH;
import static com.emmsale.event.exception.EventExceptionType.INVALID_YEAR;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
import static com.emmsale.tag.exception.TagExceptionType.NOT_FOUND_TAG;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.application.dto.ParticipantResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventTag;
import com.emmsale.event.domain.Participant;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.EventTagRepository;
import com.emmsale.event.domain.repository.ParticipantRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.member.domain.Member;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import com.emmsale.tag.exception.TagException;
import java.time.LocalDate;
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
  private final ParticipantRepository participantRepository;
  private final EventTagRepository eventTagRepository;
  private final TagRepository tagRepository;

  @Transactional(readOnly = true)
  public EventDetailResponse findEvent(final Long id) {
    final Event event = eventRepository.findById(id)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));
    return EventDetailResponse.from(event);
  }

  public List<ParticipantResponse> findParticipants(final Long eventId) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));
    return event.getParticipants().stream()
        .sorted(comparing(Participant::getId))
        .map(ParticipantResponse::from)
        .collect(toUnmodifiableList());
  }

  public Long participate(final Long eventId, final Long memberId, final Member member) {
    validateMemberNotAllowed(memberId, member);
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));

    final Participant participant = event.addParticipant(member);
    participantRepository.save(participant);
    return participant.getId();
  }

  private static void validateMemberNotAllowed(final Long memberId, final Member member) {
    if (member.isNotMe(memberId)) {
      throw new EventException(EventExceptionType.FORBIDDEN_PARTICIPATE_EVENT);
    }
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
            groupingBy(event -> event.calculateEventStatus(nowDate),
                () -> new EnumMap<>(EventStatus.class), toList())
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

  private List<EventResponse> filterEventResponsesByStatus(final String statusName,
      final EnumMap<EventStatus, List<Event>> sortAndGroupByEventStatus) {
    if (isExistStatusName(statusName)) {
      EventStatus status = EventStatus.from(statusName);
      return EventResponse.makeEventResponsesByStatus(status,
          sortAndGroupByEventStatus.get(status));
    }
    return EventResponse.mergeEventResponses(sortAndGroupByEventStatus);
  }

  private boolean isExistStatusName(final String statusName) {
    return statusName != null;
  }
}
