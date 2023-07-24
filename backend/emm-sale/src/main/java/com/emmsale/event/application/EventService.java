package com.emmsale.event.application;

import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

  private final EventRepository eventRepository;

  @Transactional(readOnly = true)
  public EventResponse findEvent(final Long id) {
    return null;
  }
}
