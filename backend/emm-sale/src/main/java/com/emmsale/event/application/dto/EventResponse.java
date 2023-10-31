package com.emmsale.event.application.dto;

import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventTag;
import com.emmsale.image.domain.AllImagesOfContent;
import com.emmsale.tag.domain.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EventResponse {

  public static final String DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss";

  private final Long id;
  private final String name;
  private final String informationUrl;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime startDate;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime endDate;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime applyStartDate;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime applyEndDate;
  private final String location;
  private final List<String> tags;
  private final String thumbnailUrl;
  private final String type;
  private final List<String> imageUrls;
  private final String organization;
  private final String paymentType;
  private final String eventMode;

  public static EventResponse from(
      final Event event,
      final AllImagesOfContent images
  ) {
    final List<String> tagNames = event.getTags().stream()
        .map(EventTag::getTag)
        .map(Tag::getName)
        .collect(toUnmodifiableList());

    return new EventResponse(
        event.getId(),
        event.getName(),
        event.getInformationUrl(),
        event.getEventPeriod().getStartDate(),
        event.getEventPeriod().getEndDate(),
        event.getEventPeriod().getApplyStartDate(),
        event.getEventPeriod().getApplyEndDate(),
        event.getLocation(),
        tagNames,
        images.extractThumbnailImage(),
        event.getType().toString(),
        images.extractInformationImages(),
        event.getOrganization(),
        event.getPaymentType().getValue(),
        event.getEventMode().getValue()
    );
  }

  public static List<EventResponse> makeEventResponsesByStatus(final List<Event> events,
      final Map<Long, AllImagesOfContent> imagesPerEventId) {
    return events.stream()
        .map(event -> toEventResponse(imagesPerEventId, event))
        .collect(Collectors.toUnmodifiableList());
  }

  private static EventResponse toEventResponse(
      final Map<Long, AllImagesOfContent> imagesPerEventId, final Event event) {
    final AllImagesOfContent allImageUrls = imagesPerEventId.get(event.getId());
    return EventResponse.from(event, allImageUrls);
  }

  public static List<EventResponse> mergeEventResponses(
      final Map<EventStatus, List<Event>> groupByEventStatus,
      final Map<Long, AllImagesOfContent> imagesPerEventId
  ) {
    return groupByEventStatus.values().stream()
        .map(events -> makeEventResponsesByStatus(events, imagesPerEventId))
        .reduce(new ArrayList<>(), (combinedEvents, eventsToAdd) -> {
          combinedEvents.addAll(eventsToAdd);
          return combinedEvents;
        });
  }
}
