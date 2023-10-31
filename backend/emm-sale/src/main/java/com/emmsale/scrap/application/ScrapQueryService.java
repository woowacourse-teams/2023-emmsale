package com.emmsale.scrap.application;

import static java.util.stream.Collectors.groupingBy;

import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.image.domain.AllImagesOfContent;
import com.emmsale.image.domain.ImageType;
import com.emmsale.image.domain.repository.ImageRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.scrap.domain.Scrap;
import com.emmsale.scrap.domain.ScrapRepository;
import java.time.LocalDateTime;
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

  public List<EventResponse> findAllScraps(final Member member) {
    //TODO : Scrap에서 event 사용해서 N+1 발생
    final List<Event> scrappedEvents = scrapRepository.findAllByMemberId(member.getId())
        .stream()
        .map(Scrap::getEvent)
        .collect(Collectors.toUnmodifiableList());

    final Map<EventStatus, List<Event>> eventGroupByStatus = scrappedEvents.stream()
        .collect(
            groupingBy(event -> event.getEventPeriod().calculateEventStatus(LocalDateTime.now())));

    return EventResponse.mergeEventResponses(eventGroupByStatus,
        makeImageUrlsPerEventId(scrappedEvents));
  }

  private Map<Long, AllImagesOfContent> makeImageUrlsPerEventId(final List<Event> events) {
    final List<Long> eventIds = events.stream()
        .map(Event::getId)
        .collect(Collectors.toUnmodifiableList());

    final Map<Long, AllImagesOfContent> imageUrlsPerEventId = new HashMap<>();
    for (Long eventId : eventIds) {
      final AllImagesOfContent images = new AllImagesOfContent(
          imageRepository.findAllByTypeAndContentId(ImageType.EVENT, eventId));
      imageUrlsPerEventId.put(eventId, images);
    }
    return imageUrlsPerEventId;
  }

}
