package com.emmsale.event.application;

import static com.emmsale.event.exception.EventExceptionType.EVENT_NOT_FOUND_EXCEPTION;

import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.Participant;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.ParticipantRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

  private final EventRepository eventRepository;
  private final ParticipantRepository participantRepository;

  @Transactional(readOnly = true)
  public EventDetailResponse findEvent(final Long id) {
    final Event event = eventRepository.findById(id)
        .orElseThrow(() -> new EventException(EVENT_NOT_FOUND_EXCEPTION));
    return EventDetailResponse.from(event);
  }

  public Long participate(final Long eventId, final Long memberId, final Member member) {
    validateMemberNotAllowd(memberId, member);
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(EVENT_NOT_FOUND_EXCEPTION));
    final Participant participant = new Participant(member, event);
    participantRepository.save(participant);
    return participant.getId();
  }

  private static void validateMemberNotAllowd(final Long memberId, final Member member) {
    if (!memberId.equals(member.getId())) {
      throw new EventException(EventExceptionType.PARTICIPATE_NOT_ALLOW_FORBIDDEN);
    }
  }
}
