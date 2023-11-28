package com.emmsale.event.application;

import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
import static com.emmsale.tag.exception.TagExceptionType.NOT_FOUND_TAG;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.application.dto.EventSearchRequest;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.EventSpecification;
import com.emmsale.event.exception.EventException;
import com.emmsale.image.application.ImageQueryService;
import com.emmsale.image.domain.AllImagesOfContent;
import com.emmsale.image.domain.ImageType;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import com.emmsale.tag.exception.TagException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventQueryService {

  private final EventRepository eventRepository;
  private final TagRepository tagRepository;
  private final ImageQueryService imageQueryService;

  public EventResponse findEvent(final Long id, final LocalDate today) {
    final Event event = eventRepository.findById(id)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));

    final AllImagesOfContent images = imageQueryService.findImagesOfContent(ImageType.EVENT,
        event.getId());
    return EventResponse.from(event, images);
  }

  public List<EventResponse> findEvents(
      final EventSearchRequest request,
      final LocalDate startDate,
      final LocalDate endDate,
      final LocalDateTime nowDateTime
  ) {
    validateTags(request.getTags());

    final Specification<Event> spec = Specification
        .where(EventSpecification.filterByCategory(request.getCategory()))
        .and(EventSpecification.filterByTags(request.getTags()))
        .and(EventSpecification.filterByPeriod(startDate, endDate))
        .and(EventSpecification.filterByNameContainsSearchKeywords(request.getKeyword()));

    final List<Event> events = eventRepository.findAll(spec);

    final EnumMap<EventStatus, List<Event>> eventsForEventStatus
        = groupByEventStatus(nowDateTime, events);

    final List<Long> eventIds = events.stream()
        .map(Event::getId)
        .collect(Collectors.toUnmodifiableList());

    return filterByStatuses(request.getStatuses(), eventsForEventStatus,
        imageQueryService.findImagesPerContentId(ImageType.EVENT, eventIds));
  }

  private void validateTags(final List<String> tagNames) {
    if (tagNames != null) {
      final List<Tag> tags = tagRepository.findByNameIn(tagNames);
      validateExistTags(tagNames, tags);
    }
  }

  private void validateExistTags(final List<String> tagNames, final List<Tag> tags) {
    if (tags.size() != tagNames.size()) {
      throw new TagException(NOT_FOUND_TAG);
    }
  }

  private EnumMap<EventStatus, List<Event>> groupByEventStatus(final LocalDateTime nowDateTime,
      final List<Event> events) {
    return events.stream()
        .sorted(comparing(event -> event.getEventPeriod().getStartDate()))
        .collect(
            groupingBy(event -> event.calculateStatus(nowDateTime),
                () -> new EnumMap<>(EventStatus.class), toList())
        );
  }

  private List<EventResponse> filterByStatuses(
      final List<EventStatus> statuses,
      final EnumMap<EventStatus, List<Event>> eventsForEventStatus,
      final Map<Long, AllImagesOfContent> imagesPerEventId
  ) {
    if (isExistStatusName(statuses)) {
      return filterEventResponseByStatuses(statuses, eventsForEventStatus, imagesPerEventId);
    }
    return EventResponse.mergeEventResponses(eventsForEventStatus, imagesPerEventId);
  }

  private boolean isExistStatusName(final List<EventStatus> statuses) {
    return statuses != null && !statuses.isEmpty();
  }

  private List<EventResponse> filterEventResponseByStatuses(
      final List<EventStatus> statuses,
      final EnumMap<EventStatus, List<Event>> eventsForEventStatus,
      final Map<Long, AllImagesOfContent> imagesPerEventId
  ) {
    return eventsForEventStatus.entrySet()
        .stream()
        .filter(entry -> statuses.contains(entry.getKey()))
        .map(entry -> EventResponse.makeEventResponsesByStatus(entry.getValue(),
            imagesPerEventId))
        .reduce(new ArrayList<>(), (combinedEvents, eventsToAdd) -> {
          combinedEvents.addAll(eventsToAdd);
          return combinedEvents;
        });
  }
}
