package com.emmsale.scrap.application;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.image.domain.Image;
import com.emmsale.image.domain.ImageType;
import com.emmsale.image.domain.repository.ImageRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.scrap.domain.Scrap;
import com.emmsale.scrap.domain.ScrapRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapQueryService {

  private final ScrapRepository scrapRepository;
  private final ImageRepository imageRepository;

  public List<EventDetailResponse> findAllScraps(final Member member) {
    //TODO : Scrap에서 event 사용해서 N+1 발생
    final List<Event> scrappedEvents = scrapRepository.findAllByMemberId(member.getId())
        .stream()
        .map(Scrap::getEvent)
        .collect(Collectors.toList());

    final Map<EventStatus, List<Event>> eventGroupByStatus = scrappedEvents.stream()
        .collect(
            groupingBy(event -> event.getEventPeriod().calculateEventStatus(LocalDateTime.now())));

    return mergeEventResponses(eventGroupByStatus, makeImageUrlsPerEventId(scrappedEvents));
  }

  private Map<Long, List<String>> makeImageUrlsPerEventId(final List<Event> events) {
    final List<Long> eventIds = events.stream()
        .map(Event::getId)
        .collect(Collectors.toList());

    Map<Long, List<String>> imageUrlsPerEventId = new HashMap<>();
    for (Long eventId : eventIds) {
      final List<String> images = imageRepository.findAllByTypeAndContentId(ImageType.EVENT,
              eventId)
          .stream()
          .sorted(comparing(Image::getOrder))
          .map(Image::getName)
          .collect(toList());
      imageUrlsPerEventId.put(eventId, images);
    }
    return imageUrlsPerEventId;
  }

  private List<EventDetailResponse> makeEventResponsesByStatus(final List<Event> events,
      final Map<Long, List<String>> imageUrlsPerEventId) {
    return events.stream()
        .map(event -> {
          final List<String> allImageUrls = imageUrlsPerEventId.get(event.getId());
          final String thumbnailImageUrl = extractThumbnailImage(allImageUrls);
          final List<String> informationImageUrls = extractInformationImages(allImageUrls);
          return EventDetailResponse.from(event, thumbnailImageUrl, informationImageUrls);
        })
        .collect(Collectors.toList());
  }

  private List<EventDetailResponse> mergeEventResponses(
      final Map<EventStatus, List<Event>> groupByEventStatus,
      final Map<Long, List<String>> imageUrlsPerEventId
  ) {
    return groupByEventStatus.values().stream()
        .map(events -> makeEventResponsesByStatus(events, imageUrlsPerEventId))
        .reduce(new ArrayList<>(), (combinedEvents, eventsToAdd) -> {
          combinedEvents.addAll(eventsToAdd);
          return combinedEvents;
        });
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

}
