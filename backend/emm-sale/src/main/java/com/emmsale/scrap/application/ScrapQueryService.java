package com.emmsale.scrap.application;

import static java.util.stream.Collectors.groupingBy;

import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.member.domain.Member;
import com.emmsale.scrap.domain.Scrap;
import com.emmsale.scrap.domain.ScrapRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapQueryService {

  private final ScrapRepository scrapRepository;

  public List<EventResponse> findAllScraps(final Member member) {
    //Scrap에서 event 사용해서 N+1 발생
    final Map<EventStatus, List<Event>> eventGroupByStatus
        = scrapRepository.findAllByMemberId(member.getId())
        .stream()
        .map(Scrap::getEvent)
        .collect(groupingBy(event -> event.getEventPeriod().calculateEventStatus(LocalDate.now())));
    return EventResponse.mergeEventResponses(LocalDate.now(), eventGroupByStatus);
//    return scraps.stream()
//        .map(ScrapResponse::from)
//        .collect(Collectors.toList());
//    return null;
  }
}
