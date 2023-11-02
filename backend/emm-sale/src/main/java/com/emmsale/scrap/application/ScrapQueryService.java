package com.emmsale.scrap.application;

import static java.util.stream.Collectors.groupingBy;

import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.image.application.ImageQueryService;
import com.emmsale.image.domain.ImageType;
import com.emmsale.member.domain.Member;
import com.emmsale.scrap.domain.Scrap;
import com.emmsale.scrap.domain.ScrapRepository;
import java.time.LocalDateTime;
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
  private final ImageQueryService imageQueryService;

  public List<EventResponse> findAllScraps(final Member member) {
    //TODO : Scrap에서 event 사용해서 N+1 발생
    final List<Event> scrappedEvents = scrapRepository.findAllByMemberId(member.getId())
        .stream()
        .map(Scrap::getEvent)
        .collect(Collectors.toUnmodifiableList());

    final Map<EventStatus, List<Event>> eventGroupByStatus = scrappedEvents.stream()
        .collect(
            groupingBy(event -> event.getEventPeriod().calculateEventStatus(LocalDateTime.now())));
    final List<Long> eventIds = scrappedEvents.stream()
        .map(Event::getId)
        .collect(Collectors.toUnmodifiableList());
    return EventResponse.mergeEventResponses(eventGroupByStatus,
        imageQueryService.findImagesPerContentId(ImageType.EVENT, eventIds));
  }

}
