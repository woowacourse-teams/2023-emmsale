package com.emmsale.scrap.application;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
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

  public void append(final Member member, final ScrapRequest scrapRequest) {
    final Long memberId = member.getId();
    final Long eventId = scrapRequest.getEventId();

    validateAlreadyScraped(memberId, eventId);

    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(EventExceptionType.NOT_FOUND_EVENT));

    scrapRepository.save(new Scrap(memberId, event));
  }

  public void deleteScrap(final Member member, final Long scrapId) {
    final Scrap scrap = scrapRepository.findById(scrapId)
        .orElseThrow(() -> new ScrapException(ScrapExceptionType.NOT_FOUND_SCRAP));

    validateScrapOwner(member, scrap);

    scrapRepository.delete(scrap);
  }

  private void validateAlreadyScraped(final Long memberId, final Long eventId) {
    if (scrapRepository.existsByMemberIdAndEventId(memberId, eventId)) {
      throw new ScrapException(ScrapExceptionType.ALREADY_EXIST_SCRAP);
    }
  }

  private void validateScrapOwner(final Member member, final Scrap scrap) {
    if (scrap.isNotOwner(member.getId())) {
      throw new ScrapException(ScrapExceptionType.FORBIDDEN_NOT_OWNER);
    }
  }
}
