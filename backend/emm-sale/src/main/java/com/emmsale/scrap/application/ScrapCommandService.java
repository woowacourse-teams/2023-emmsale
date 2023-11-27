package com.emmsale.scrap.application;

import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.image.application.ImageQueryService;
import com.emmsale.image.domain.ImageType;
import com.emmsale.member.domain.Member;
import com.emmsale.scrap.application.dto.ScrapRequest;
import com.emmsale.scrap.domain.Scrap;
import com.emmsale.scrap.domain.ScrapRepository;
import com.emmsale.scrap.exception.ScrapException;
import com.emmsale.scrap.exception.ScrapExceptionType;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ScrapCommandService {

  private final ScrapRepository scrapRepository;
  private final EventRepository eventRepository;
  private final ImageQueryService imageQueryService;


  public EventResponse append(final Member member, final ScrapRequest scrapRequest) {
    final Long memberId = member.getId();
    final Long eventId = scrapRequest.getEventId();

    validateAlreadyScraped(memberId, eventId);

    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(EventExceptionType.NOT_FOUND_EVENT));

    scrapRepository.save(new Scrap(memberId, event));
    return EventResponse.from(event,
        imageQueryService.findImagesOfContent(ImageType.EVENT, event.getId()));
  }

  public EventResponse deleteScrap(final Member member, final Long eventId) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(EventExceptionType.NOT_FOUND_EVENT));
    scrapRepository.deleteByMemberIdAndEventId(member.getId(), eventId);
    return EventResponse.from(event,
        imageQueryService.findImagesOfContent(ImageType.EVENT, eventId));

  }

  private void validateAlreadyScraped(final Long memberId, final Long eventId) {
    if (scrapRepository.existsByMemberIdAndEventId(memberId, eventId)) {
      throw new ScrapException(ScrapExceptionType.ALREADY_EXIST_SCRAP);
    }
  }
}
