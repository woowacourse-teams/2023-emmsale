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

import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventType;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.EventTagRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.event_publisher.EventPublisher;
import com.emmsale.image.application.ImageCommandService;
import com.emmsale.image.domain.Image;
import com.emmsale.image.domain.ImageType;
import com.emmsale.image.domain.repository.ImageRepository;
import com.emmsale.tag.application.dto.TagRequest;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import com.emmsale.tag.exception.TagException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

  private static final String MIN_DATE = "2000-01-01";
  private static final String MAX_DATE = "2999-12-31";

  private final EventRepository eventRepository;
  private final EventTagRepository eventTagRepository;
  private final TagRepository tagRepository;
  private final EventPublisher eventPublisher;
  private final ImageCommandService imageCommandService;
  private final ImageRepository imageRepository;

  @Transactional(readOnly = true)
  public EventDetailResponse findEvent(final Long id, final LocalDate today) {
    final Event event = eventRepository.findById(id)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));

    final List<String> imageUrls = imageRepository
        .findAllByTypeAndContentId(ImageType.EVENT, event.getId())
        .stream()
        .sorted(comparing(Image::getOrder))
        .map(Image::getName)
        .collect(toList());
    final String thumbnailImageUrl = extractThumbnailImage(imageUrls);
    final List<String> informationImageUrls = extractInformationImages(imageUrls);
    return EventDetailResponse.from(event, thumbnailImageUrl, informationImageUrls);
  }

  private String extractThumbnailImage(final List<String> imageUrls) {
    if (imageUrls.isEmpty()) {
      return null;
    }
    return imageUrls.get(0);
  }

  private List<String> extractInformationImages(final List<String> imageUrls) {
    if (imageUrls.size() <= 1) {
      return Collections.emptyList();
    }
    return imageUrls.subList(1, imageUrls.size());
  }

  @Transactional(readOnly = true)
  public List<EventResponse> findEvents(final EventType category,
      final LocalDate nowDate, final String startDate, final String endDate,
      final List<String> tagNames, final List<EventStatus> statuses, final String keyword) {
    Specification<Event> spec = Specification.where(filterByCategory(category));

    spec = filterByTagIfExist(tagNames, spec);
    spec = filterByDateIfExist(startDate, endDate, spec);
    spec = filterByKeywordIfExist(keyword, spec);

    final List<Event> events = eventRepository.findAll(spec);

    final EnumMap<EventStatus, List<Event>> eventsForEventStatus
        = groupByEventStatus(nowDate, events);

    return filterByStatuses(statuses, eventsForEventStatus, makeImageUrlPerEventId(events));
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

  // TODO: 2023/09/27 코드 중복 제거(ScrapService)
  private Map<Long, String> makeImageUrlPerEventId(final List<Event> events) {
    final List<Long> scrappedEventIds = events.stream()
        .map(Event::getId)
        .collect(Collectors.toList());
    final List<Image> images = imageRepository.findAllThumbnailByEventIdIn(scrappedEventIds);
    Map<Long, String> imageUrlPerEventId = new HashMap<>();
    for (Image image : images) {
      imageUrlPerEventId.put(image.getContentId(), image.getName());
    }
    return imageUrlPerEventId;
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

  private EnumMap<EventStatus, List<Event>> groupByEventStatus(final LocalDate nowDate,
      final List<Event> events) {
    return events.stream()
        .sorted(comparing(event -> event.getEventPeriod().getStartDate()))
        .collect(
            groupingBy(event -> event.getEventPeriod().calculateEventStatus(nowDate),
                () -> new EnumMap<>(EventStatus.class), toList())
        );
  }

  private List<EventResponse> filterByStatuses(
      final List<EventStatus> statuses,
      final EnumMap<EventStatus, List<Event>> eventsForEventStatus,
      final Map<Long, String> imageUrlPerEventId
  ) {
    if (isExistStatusName(statuses)) {
      return filterEventResponseByStatuses(statuses, eventsForEventStatus, imageUrlPerEventId);
    }
    return EventResponse.mergeEventResponses(eventsForEventStatus, imageUrlPerEventId);
  }

  private boolean isExistStatusName(final List<EventStatus> statuses) {
    return statuses != null;
  }

  private List<EventResponse> filterEventResponseByStatuses(
      final List<EventStatus> statuses,
      final EnumMap<EventStatus, List<Event>> eventsForEventStatus,
      final Map<Long, String> imageUrlPerEventId
  ) {
    return eventsForEventStatus.entrySet()
        .stream()
        .filter(entry -> statuses.contains(entry.getKey()))
        .map(
            entry -> EventResponse.makeEventResponsesByStatus(entry.getValue(), imageUrlPerEventId))
        .reduce(new ArrayList<>(), (combinedEvents, eventsToAdd) -> {
          combinedEvents.addAll(eventsToAdd);
          return combinedEvents;
        });
  }

  public EventDetailResponse addEvent(final EventDetailRequest request,
      final List<MultipartFile> images) {
    final Event event = eventRepository.save(request.toEvent());
    final List<Tag> tags = findAllPersistTagsOrElseThrow(request.getTags());
    event.addAllEventTags(tags);

    final List<String> imageUrls = imageCommandService
        .saveImages(ImageType.EVENT, event.getId(), images)
        .stream()
        .sorted(comparing(Image::getOrder))
        .map(Image::getName)
        .collect(toList());

    eventPublisher.publish(event);
    final String thumbnailImageUrl = extractThumbnailImage(imageUrls);
    final List<String> informationImageUrls = extractInformationImages(imageUrls);
    return EventDetailResponse.from(event, thumbnailImageUrl, informationImageUrls);
  }

  public EventDetailResponse updateEvent(final Long eventId, final EventDetailRequest request,
      final List<MultipartFile> images) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));

    final List<Tag> tags = findAllPersistTagsOrElseThrow(request.getTags());

    // TODO: 2023/09/25 더 좋은 방법을 고민해보기
    eventTagRepository.deleteAllByEventId(eventId);
    final Event updatedEvent = event.updateEventContent(
        request.getName(),
        request.getLocation(),
        request.getStartDateTime(),
        request.getEndDateTime(),
        request.getApplyStartDateTime(),
        request.getApplyEndDateTime(),
        request.getInformationUrl(),
        tags,
        request.getType(),
        request.getEventMode(),
        request.getPaymentType(),
        request.getOrganization()
    );
    imageCommandService.deleteImages(ImageType.EVENT, eventId);
    final List<String> imageUrls = imageCommandService
        .saveImages(ImageType.EVENT, event.getId(), images)
        .stream()
        .sorted(comparing(Image::getOrder))
        .map(Image::getName)
        .collect(toList());
    final String thumbnailImageUrl = extractThumbnailImage(imageUrls);
    final List<String> informationImageUrls = extractInformationImages(imageUrls);
    return EventDetailResponse.from(updatedEvent, thumbnailImageUrl, informationImageUrls);
  }

  public void deleteEvent(final Long eventId) {
    if (!eventRepository.existsById(eventId)) {
      throw new EventException(NOT_FOUND_EVENT);
    }
    imageCommandService.deleteImages(ImageType.EVENT, eventId);
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
}
