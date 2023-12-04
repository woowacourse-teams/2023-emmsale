package com.emmsale.event.application;

import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
import static com.emmsale.tag.exception.TagExceptionType.NOT_FOUND_TAG;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.application.dto.EventSearchRequest;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.Events;
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
import java.util.EnumMap;
import java.util.List;
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

    final Specification<Event> spec = specificationEvents(request, startDate, endDate);

    final Events events = new Events(eventRepository.findAll(spec));
    final EnumMap<EventStatus, List<Event>> map = events.groupByEventStatus(
        request.getStatuses(),
        nowDateTime
    );
    final List<Long> eventIds = events.getEventIds();

    return EventResponse.mergeEventResponses(
        map,
        imageQueryService.findImagesPerContentId(ImageType.EVENT, eventIds)
    );
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

  private static Specification<Event> specificationEvents(final EventSearchRequest request,
      final LocalDate startDate, final LocalDate endDate) {
    return Specification
        .where(EventSpecification.filterByCategory(request.getCategory()))
        .and(EventSpecification.filterByTags(request.getTags()))
        .and(EventSpecification.filterByPeriod(startDate, endDate))
        .and(EventSpecification.filterByNameContainsSearchKeywords(request.getKeyword()));
  }
}
