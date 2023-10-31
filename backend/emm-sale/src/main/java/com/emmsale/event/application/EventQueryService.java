package com.emmsale.event.application;

import static com.emmsale.event.domain.repository.EventSpecification.filterByCategory;
import static com.emmsale.event.domain.repository.EventSpecification.filterByNameContainsSearchKeywords;
import static com.emmsale.event.domain.repository.EventSpecification.filterByPeriod;
import static com.emmsale.event.domain.repository.EventSpecification.filterByTags;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
import static com.emmsale.tag.exception.TagExceptionType.NOT_FOUND_TAG;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventType;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.image.domain.AllImagesOfContent;
import com.emmsale.image.domain.Image;
import com.emmsale.image.domain.ImageType;
import com.emmsale.image.domain.repository.ImageRepository;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import com.emmsale.tag.exception.TagException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
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

  private static final String MIN_DATE = "2000-01-01";
  private static final String MAX_DATE = "2999-12-31";

  private final EventRepository eventRepository;
  private final TagRepository tagRepository;
  private final ImageRepository imageRepository;

  public EventResponse findEvent(final Long id, final LocalDate today) {
    final Event event = eventRepository.findById(id)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));

    final AllImagesOfContent images = new AllImagesOfContent(imageRepository
        .findAllByTypeAndContentId(ImageType.EVENT, event.getId())
        .stream()
        .sorted(comparing(Image::getOrder))
        .collect(toList()));
    final String thumbnailImageUrl = images.extractThumbnailImage();
    final List<String> informationImageUrls = images.extractInformationImages();
    return EventResponse.from(event, thumbnailImageUrl, informationImageUrls);
  }

  public List<EventResponse> findEvents(final EventType category,
      final LocalDateTime nowDateTime, final String startDate, final String endDate,
      final List<String> tagNames, final List<EventStatus> statuses, final String keyword) {
    Specification<Event> spec = (root, query, criteriaBuilder) -> null;
    spec = filterByCategoryIfExist(category, spec);
    spec = filterByTagIfExist(tagNames, spec);
    spec = filterByDateIfExist(startDate, endDate, spec);
    spec = filterByKeywordIfExist(keyword, spec);

    final List<Event> events = eventRepository.findAll(spec);

    final EnumMap<EventStatus, List<Event>> eventsForEventStatus
        = groupByEventStatus(nowDateTime, events);

    return filterByStatuses(statuses, eventsForEventStatus, makeImagesPerEventId(events));
  }

  private Specification<Event> filterByCategoryIfExist(final EventType category,
      Specification<Event> spec) {
    if (isExistCategory(category)) {
      spec = spec.and(filterByCategory(category));
    }
    return spec;
  }

  private boolean isExistCategory(final EventType category) {
    return category != null;
  }

  private Specification<Event> filterByTagIfExist(final List<String> tagNames,
      Specification<Event> spec) {
    if (isExistTagNames(tagNames)) {
      validateTags(tagNames);
      spec = spec.and(filterByTags(tagNames));
    }
    return spec;
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

  private Specification<Event> filterByDateIfExist(final String startDate, final String endDate,
      Specification<Event> spec) {
    if (isExistFilterDate(startDate, endDate)) {
      final LocalDateTime startDateTime = validateStartDate(startDate);
      final LocalDateTime endDateTime = validateEndDate(endDate);
      validateEndDateAfterDateStart(startDateTime, endDateTime);
      spec = spec.and(filterByPeriod(startDateTime, endDateTime));
    }
    return spec;
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
    } catch (final DateTimeParseException exception) {
      throw new EventException(EventExceptionType.INVALID_DATE_FORMAT);
    }
  }

  private LocalDateTime validateEndDate(final String date) {
    try {
      if (date == null) {
        return LocalDate.parse(MAX_DATE).atTime(23, 59, 59);
      }
      return LocalDate.parse(date).atTime(23, 59, 59);
    } catch (final DateTimeParseException exception) {
      throw new EventException(EventExceptionType.INVALID_DATE_FORMAT);
    }
  }

  private void validateEndDateAfterDateStart(final LocalDateTime startDate,
      final LocalDateTime endDate) {
    if (endDate.isBefore(startDate)) {
      throw new EventException(EventExceptionType.START_DATE_AFTER_END_DATE);
    }
  }

  private Specification<Event> filterByKeywordIfExist(final String keyword,
      Specification<Event> spec) {
    if (isExistKeyword(keyword)) {
      final String[] keywords = keyword.trim().split(" ");
      spec = spec.and(filterByNameContainsSearchKeywords(keywords));
    }
    return spec;
  }

  private boolean isExistKeyword(final String keyword) {
    return keyword != null && !keyword.isBlank();
  }

  private EnumMap<EventStatus, List<Event>> groupByEventStatus(final LocalDateTime nowDateTime,
      final List<Event> events) {
    return events.stream()
        .sorted(comparing(event -> event.getEventPeriod().getStartDate()))
        .collect(
            groupingBy(event -> event.getEventPeriod().calculateEventStatus(nowDateTime),
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
    return statuses != null;
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

  // TODO: 2023/09/27 코드 중복 제거(ScrapService)
  private Map<Long, AllImagesOfContent> makeImagesPerEventId(final List<Event> events) {
    final List<Long> eventIds = events.stream()
        .map(Event::getId)
        .collect(Collectors.toList());

    Map<Long, AllImagesOfContent> imagesPerEventId = new HashMap<>();
    for (Long eventId : eventIds) {
      final AllImagesOfContent images = new AllImagesOfContent(
          imageRepository.findAllByTypeAndContentId(ImageType.EVENT, eventId));
      imagesPerEventId.put(eventId, images);
    }
    return imagesPerEventId;
  }
}
