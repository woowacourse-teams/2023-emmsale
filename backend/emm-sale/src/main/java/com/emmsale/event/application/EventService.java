package com.emmsale.event.application;

import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
import static com.emmsale.tag.exception.TagExceptionType.NOT_FOUND_TAG;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.application.dto.ParticipantResponse;
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
import java.time.format.DateTimeParseException;
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

  private final EventRepository eventRepository;
  private final ParticipantRepository participantRepository;
  private final EventTagRepository eventTagRepository;
  private final TagRepository tagRepository;

  private static void validateMemberNotAllowed(final Long memberId, final Member member) {
    if (member.isNotMe(memberId)) {
      throw new EventException(EventExceptionType.FORBIDDEN_PARTICIPATE_EVENT);
    }
  }

  private static boolean isExistFilterDate(final String startDate, final String endDate) {
    return startDate != null || endDate != null;
  }

  @Transactional(readOnly = true)
  public EventDetailResponse findEvent(final Long id, final LocalDate today) {
    final Event event = eventRepository.findById(id)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));
    return EventDetailResponse.from(event, today);
  }

  public Long participate(final Long eventId, final Long memberId, final Member member) {
    validateMemberNotAllowed(memberId, member);
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));

    final Participant participant = event.addParticipant(member);
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
              throw new EventException(EventExceptionType.NOT_FOUND_PARTICIPANT);
            });
  }

  @Transactional(readOnly = true)
  public List<EventResponse> findEvents(final EventType category, final LocalDate nowDate,
      final String startDate, final String endDate,
      final List<String> tagNames, final List<EventStatus> statuses) {
    List<Event> events = eventRepository.findEventsByType(category);

    if (isExistTagNames(tagNames)) {
      events = filterByTags(events, tagNames);
    }
    if (isExistFilterDate(startDate, endDate)) {
      events = filterByPeriod(events, startDate, endDate);
    }
    final EnumMap<EventStatus, List<Event>> eventsForEventStatus
        = groupByEventStatus(nowDate, events);

    return filterByStatuses(nowDate, statuses, eventsForEventStatus);
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

  private boolean isExistTagNames(final List<String> tagNames) {
    return tagNames != null;
  }

  private List<Event> filterByTags(List<Event> events, final List<String> tagNames) {
    List<Tag> tags = tagNames.stream().map(tagName ->
        tagRepository.findByName(tagName)
            .orElseThrow(() -> new TagException(NOT_FOUND_TAG))
    ).collect(toList());
    List<Event> tagedEvents = eventTagRepository.findEventTagsByTagIn(tags)
        .stream()
        .map(EventTag::getEvent).collect(toList());
    return events.stream()
        .filter(event -> tagedEvents.contains(event)).collect(toList());

//    return eventTagRepository.findEventTagsByTagIn(tags)
//        .stream()
//        .map(EventTag::getEvent)
//        .distinct()
//        .collect(toList());
  }

  private List<Event> filterByPeriod(final List<Event> events, final String startDate,
      final String endDate) {
    LocalDate filterStart = validateStartDate(startDate);
    LocalDate filterEnd = validateEndDate(endDate);
    validateEndDateAfterDateStart(filterStart, filterEnd);

    return events.stream()
        .filter(event -> isOverlapToMonth(filterStart, filterEnd,
            event.getStartDate().toLocalDate(), event.getEndDate().toLocalDate())
        )
        .collect(toList());
  }


  private LocalDate validateStartDate(final String date) {
    try {
      if (date == null) {
        return LocalDate.MIN;
      }
      return LocalDate.parse(date);
    } catch (DateTimeParseException exception) {
      throw new EventException(EventExceptionType.INVALID_DATE_FORMAT);
    }
  }

  private LocalDate validateEndDate(final String date) {
    try {
      if (date == null) {
        return LocalDate.MAX;
      }
      return LocalDate.parse(date);
    } catch (DateTimeParseException exception) {
      throw new EventException(EventExceptionType.INVALID_DATE_FORMAT);
    }
  }

  private void validateEndDateAfterDateStart(LocalDate startDate, LocalDate endDate) {
    if (endDate.isBefore(startDate)) {
      throw new EventException(EventExceptionType.START_DATE_AFTER_END_DATE);
    }
  }

  private boolean isOverlapToMonth(final LocalDate startDate, final LocalDate endDate,
      final LocalDate eventStart, final LocalDate eventEnd) {
    return
        (isBeforeOrEquals(eventStart, endDate) && isBeforeOrEquals(startDate, eventEnd))
            || (isBeforeOrEquals(startDate, eventStart) && isBeforeOrEquals(eventStart, endDate))
            || (isBeforeOrEquals(startDate, eventEnd) && isBeforeOrEquals(eventEnd, endDate));
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

  private List<EventResponse> filterByStatuses(LocalDate today,
      final List<EventStatus> statuses,
      final EnumMap<EventStatus, List<Event>> eventsForEventStatus) {
    if (isExistStatusName(statuses)) {
      return filterEventResponseByStatuses(today, statuses, eventsForEventStatus);
    }
    return EventResponse.mergeEventResponses(today, eventsForEventStatus);
  }

  private boolean isExistStatusName(final List<EventStatus> statuses) {
    return statuses != null;
  }

  private List<EventResponse> filterEventResponseByStatuses(final LocalDate today,
      final List<EventStatus> statuses,
      final EnumMap<EventStatus, List<Event>> eventsForEventStatus) {
    return eventsForEventStatus.entrySet()
        .stream()
        .filter(entry -> statuses.contains(entry.getKey()))
        .map(entry -> EventResponse.makeEventResponsesByStatus(today, entry.getKey(),
            entry.getValue()))
        .reduce(new ArrayList<>(), (combinedEvents, eventsToAdd) -> {
          combinedEvents.addAll(eventsToAdd);
          return combinedEvents;
        });
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

  public Boolean isAlreadyParticipate(final Long eventId, final Long memberId) {
    return participantRepository.existsByEventIdAndMemberId(eventId, memberId);
  }
}
