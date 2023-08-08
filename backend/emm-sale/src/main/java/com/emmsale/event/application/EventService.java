package com.emmsale.event.application;

import static com.emmsale.event.domain.repository.EventSpecification.filterByCategory;
import static com.emmsale.event.domain.repository.EventSpecification.filterByTags;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
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
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventType;
import com.emmsale.event.domain.Participant;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.EventSpecification;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

  private static final String MIN_DATE = "2000-01-01";
  private static final String MAX_DATE = "2999-12-31";

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
              throw new EventException(EventExceptionType.NOT_FOUND_PARTICIPANT);
            });
  }

  @Transactional(readOnly = true)
  public List<EventResponse> findEvents(final EventType category,
      final LocalDate nowDate, final String startDate, final String endDate,
      final List<String> tagNames, final List<EventStatus> statuses) {
    Specification<Event> spec = Specification.where(filterByCategory(category));

    if (isExistTagNames(tagNames)) {
      validateTags(tagNames);
      spec = spec.and(filterByTags(tagNames));
    }

    if (isExistFilterDate(startDate, endDate)) {
      LocalDateTime startDateTime = validateStartDate(startDate);
      LocalDateTime endDateTime = validateEndDate(endDate);
      validateEndDateAfterDateStart(startDateTime, endDateTime);
      spec = spec.and(EventSpecification.filterByPeriod(startDateTime, endDateTime));
    }
    List<Event> events = eventRepository.findAll(spec);
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

  private void validateTags(final List<String> tagNames) {
    final List<Tag> tags = tagRepository.findByNameIn(tagNames);
    if (tags.size() != tagNames.size()) {
      throw new TagException(NOT_FOUND_TAG);
    }
  }

  private boolean isExistFilterDate(final String startDate, final String endDate) {
    return startDate != null || endDate != null;
  }

  private LocalDateTime validateStartDate(final String date) {
    try {
      if (date == null) {
        return LocalDate.parse(MIN_DATE).atStartOfDay();
      }
      return LocalDate.parse(date).atStartOfDay();
    } catch (DateTimeParseException exception) {
      throw new EventException(EventExceptionType.INVALID_DATE_FORMAT);
    }
  }

  private LocalDateTime validateEndDate(final String date) {
    try {
      if (date == null) {
        return LocalDate.parse(MAX_DATE).atTime(23, 59, 59);
      }
      return LocalDate.parse(date).atTime(23, 59, 59);
    } catch (DateTimeParseException exception) {
      throw new EventException(EventExceptionType.INVALID_DATE_FORMAT);
    }
  }

  private void validateEndDateAfterDateStart(LocalDateTime startDate, LocalDateTime endDate) {
    if (endDate.isBefore(startDate)) {
      throw new EventException(EventExceptionType.START_DATE_AFTER_END_DATE);
    }
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

  @Transactional(readOnly = true)
  public Boolean isAlreadyParticipate(final Long eventId, final Long memberId) {
    return participantRepository.existsByEventIdAndMemberId(eventId, memberId);
  }
}
