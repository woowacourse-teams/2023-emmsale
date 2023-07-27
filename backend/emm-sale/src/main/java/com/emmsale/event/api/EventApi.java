package com.emmsale.event.api;

import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.net.URI.create;

import com.emmsale.event.application.EventService;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.ParticipantResponse;
import java.util.List;
import com.emmsale.event.application.dto.EventParticipateRequest;
import com.emmsale.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EventApi {

  private final EventService eventService;

  @GetMapping("/events/{id}")
  public ResponseEntity<EventDetailResponse> findEventById(@PathVariable final String id) {
    return ResponseEntity.ok(eventService.findEvent(parseLong(id)));
  }

  @PostMapping("/events/{eventId}/participants")
  public ResponseEntity<String> participateEvent(
      @PathVariable final String eventId,
      @RequestBody final EventParticipateRequest request,
      final Member member
  ) {
    final Long participantId = eventService.participate(
        parseLong(eventId),
        request.getMemberId(),
        member
    );

    return ResponseEntity
        .created(create(format("/events/%s/participants/%s", eventId, participantId)))
        .build();
  }

  @GetMapping("/events/{id}/participants")
  public ResponseEntity<List<ParticipantResponse>> findParticipants(@PathVariable final Long id) {
    final List<ParticipantResponse> responses = eventService.findParticipants(id);
    return ResponseEntity.ok(responses);
  }
}
