package com.emmsale.event.application;

import static com.emmsale.event.exception.EventExceptionType.INVALID_MONTH;
import static com.emmsale.event.exception.EventExceptionType.INVALID_YEAR;
import static com.emmsale.event.exception.EventExceptionType.INVALID_YEAR_AND_MONTH;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_PARTICIPANT;
import static com.emmsale.tag.exception.TagExceptionType.NOT_FOUND_TAG;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.EventParticipateRequest;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.application.dto.ParticipantResponse;
import com.emmsale.event.application.dto.ParticipateUpdateRequest;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventTag;
import com.emmsale.event.domain.EventType;
import com.emmsale.event.domain.Participant;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.EventTagRepository;
import com.emmsale.event.domain.repository.ParticipantRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.member.domain.Member;
import com.emmsale.tag.application.dto.TagRequest;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import com.emmsale.tag.exception.TagException;
import java.time.LocalDate;
import java.util.ArrayList;
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

  private static void validateMemberNotAllowed(final Long memberId, final Member member) {
    if (member.isNotMe(memberId)) {
      throw new EventException(EventExceptionType.FORBIDDEN_PARTICIPATE_EVENT);
    }
  }

  @Transactional(readOnly = true)
  public EventDetailResponse findEvent(final Long id, final LocalDate today) {
    final Event event = eventRepository.findById(id)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));
    return EventDetailResponse.from(event, today);
  }

  public Long participate(
      final Long eventId,
      final EventParticipateRequest request,
      final Member member
  ) {
    final Long memberId = request.getMemberId();
    final String content = request.getContent();
    validateMemberNotAllowed(memberId, member);
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));

    final Participant participant = event.addParticipant(member, content);
    participantRepository.save(participant);
    return participant.getId();
  }

  public void cancelParticipate(final Long eventId, final Long memberId, final Member member) {
    validateMemberNotAllowed(memberId, member);
    if (!eventRepository.existsById(eventId)) {
      throw new EventException(NOT_FOUND_EVENT);
    }

    participantRepository
        .findByMemberIdAndEventId(memberId, eventId)
        .ifPresentOrElse(
            participant -> participantRepository.deleteById(participant.getId()),
            () -> {
              throw new EventException(NOT_FOUND_PARTICIPANT);
            });
  }

  @Transactional(readOnly = true)
  public List<EventResponse> findEvents(final EventType categoryName, final LocalDate nowDate,
      final Integer year, final Integer month,
      final String tagName, final String statusName) {
    List<Event> events = eventRepository.findEventsByType(categoryName);

    if (isExistTagName(tagName)) {
      events = filterByTag(tagName);
    }
    if (isExistBothYearAndMonth(year, month)) {
      events = filterByPeriod(events, year, month);
    }
    final EnumMap<EventStatus, List<Event>> eventsForEventStatus
        = groupByEventStatus(nowDate, events);

    return filterEventResponsesByStatus(nowDate, statusName, eventsForEventStatus);
  }

  @Transactional(readOnly = true)
  public List<ParticipantResponse> findParticipants(final Long eventId) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));
    return event.getParticipants().stream()
        .sorted(comparing(Participant::getId))
        .map(ParticipantResponse::from)
        .collect(toUnmodifiableList());
  }

  private boolean isExistTagName(final String tagName) {
    return tagName != null;
  }

  private List<Event> filterByTag(final String tagName) {
    final Tag tag = tagRepository.findByName(tagName)
        .orElseThrow(() -> new TagException(NOT_FOUND_TAG));
    return eventTagRepository.findEventTagsByTag(tag)
        .stream()
        .map(EventTag::getEvent)
        .collect(toList());
  }

  private boolean isExistBothYearAndMonth(final Integer year, final Integer month) {
    if ((year == null && month != null) || (year != null && month == null)) {
      throw new EventException(INVALID_YEAR_AND_MONTH);
    }
    return (year != null && month != null);
  }

  private List<Event> filterByPeriod(final List<Event> events, final int year, final int month) {
    validateYearAndMonth(year, month);
    return events.stream()
        .filter(event -> isOverlapToMonth(year, month,
            event.getStartDate().toLocalDate(), event.getEndDate().toLocalDate())
        )
        .collect(toList());
  }

  private void validateYearAndMonth(final int year, final int month) {
    if (year < MIN_YEAR) {
      throw new EventException(INVALID_YEAR);
    }
    if (month < MIN_MONTH || month > MAX_MONTH) {
      throw new EventException(INVALID_MONTH);
    }
  }

  private boolean isOverlapToMonth(final int year, final int month,
      final LocalDate eventStart, final LocalDate eventEnd) {
    final LocalDate monthStart = LocalDate.of(year, month, 1);
    final LocalDate monthEnd = LocalDate.of(year, month, monthStart.lengthOfMonth());

    return (isBeforeOrEquals(eventStart, monthEnd) && isBeforeOrEquals(monthStart, eventEnd))
        || (isBeforeOrEquals(monthStart, eventStart) && isBeforeOrEquals(eventStart, monthEnd))
        || (isBeforeOrEquals(monthStart, eventEnd) && isBeforeOrEquals(eventEnd, monthEnd));
  }

  private boolean isBeforeOrEquals(final LocalDate criteria, final LocalDate comparison) {
    return criteria.isBefore(comparison) || criteria.isEqual(comparison);
  }

  private EnumMap<EventStatus, List<Event>> groupByEventStatus(final LocalDate nowDate,
      final List<Event> events) {
    return events.stream()
        .sorted(comparing(Event::getStartDate))
        .collect(
            groupingBy(event -> event.calculateEventStatus(nowDate),
                () -> new EnumMap<>(EventStatus.class), toList())
        );
  }

  private List<EventResponse> filterEventResponsesByStatus(LocalDate today, final String statusName,
      final EnumMap<EventStatus, List<Event>> eventsForEventStatus) {
    if (isExistStatusName(statusName)) {
      EventStatus status = EventStatus.from(statusName);
      List<Event> filteredEvents = eventsForEventStatus.get(status);
      if (cannotFoundKeyStatus(filteredEvents)) {
        return List.of();
      }
      return EventResponse.makeEventResponsesByStatus(today, status, filteredEvents);
    }
    return EventResponse.mergeEventResponses(today, eventsForEventStatus);
  }

  private boolean cannotFoundKeyStatus(final List<Event> filteredEvents) {
    return filteredEvents == null;
  }

  private boolean isExistStatusName(final String statusName) {
    return statusName != null;
  }

  public EventDetailResponse addEvent(final EventDetailRequest request, final LocalDate today) {
    final Event event = eventRepository.save(request.toEvent());

    final List<Tag> tags = findAllPersistTagsOrElseThrow(request.getTags());

    event.addAllEventTags(tags);

    return EventDetailResponse.from(event, today);
  }

  public EventDetailResponse updateEvent(final Long eventId, final EventDetailRequest request,
      final LocalDate today) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));

    final List<Tag> tags = findAllPersistTagsOrElseThrow(request.getTags());

    eventTagRepository.deleteAllByEventId(eventId);

    final Event updatedEvent = event.updateEventContent(
        request.getName(),
        request.getLocation(),
        request.getStartDateTime(),
        request.getEndDateTime(),
        request.getInformationUrl(),
        tags
    );

    return EventDetailResponse.from(updatedEvent, today);
  }

  public void deleteEvent(final Long eventId) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));

    eventRepository.deleteById(eventId);
  }

  private List<Tag> findAllPersistTagsOrElseThrow(final List<TagRequest> tags) {
    if (tags == null || tags.isEmpty()) {
      return new ArrayList<>();
    }

    return tags.stream()
        .map(tag -> tagRepository.findByName(tag.getName())
            .orElseThrow(() -> new EventException(EventExceptionType.NOT_FOUND_TAG)))
        .collect(toList());
  }

  @Transactional(readOnly = true)
  public Boolean isAlreadyParticipate(final Long eventId, final Long memberId) {
    return participantRepository.existsByEventIdAndMemberId(eventId, memberId);
  }

  public void updateParticipant(
      final Long eventId,
      final Long participantId,
      final ParticipateUpdateRequest request,
      final Member member
  ) {
    final Participant participant = participantRepository.findById(participantId)
        .orElseThrow(() -> new EventException(NOT_FOUND_PARTICIPANT));
    participant.validateEvent(eventId);
    participant.validateOwner(member);
    participant.updateContent(request.getContent());
  }
}
