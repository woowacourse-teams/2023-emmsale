package com.emmsale.event.api;

import com.emmsale.event.application.EventService;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.ParticipantResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EventApi {

  private final EventService eventService;

  @GetMapping("/events/{id}")
  public ResponseEntity<EventDetailResponse> findEventById(@PathVariable final String id) {
    return ResponseEntity.ok(eventService.findEvent(Long.parseLong(id)));
  }

  @GetMapping("/events/{id}/participants")
  public ResponseEntity<List<ParticipantResponse>> findParticipants(@PathVariable final Long id) {
    final List<ParticipantResponse> responses = eventService.findParticipants(id);
    return ResponseEntity.ok(responses);
  }
}
