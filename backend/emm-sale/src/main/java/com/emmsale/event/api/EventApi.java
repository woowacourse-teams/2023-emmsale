package com.emmsale.event.api;

import static java.lang.String.format;
import static java.net.URI.create;

import com.emmsale.event.application.EventService;
import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.EventParticipateRequest;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.application.dto.ParticipantResponse;
import com.emmsale.member.domain.Member;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventApi {

  private final EventService eventService;

  @GetMapping("/{id}")
  public ResponseEntity<EventDetailResponse> findEventById(@PathVariable final Long id) {
    return ResponseEntity.ok(eventService.findEvent(id));
  }

  @PostMapping("/{eventId}/participants")
  public ResponseEntity<String> participateEvent(
      @PathVariable final Long eventId,
      @RequestBody final EventParticipateRequest request,
      final Member member
  ) {
    final Long participantId = eventService.participate(
        eventId,
        request.getMemberId(),
        member
    );

    return ResponseEntity
        .created(create(format("/events/%s/participants/%s", eventId, participantId)))
        .build();
  }

  @GetMapping("/{id}/participants")
  public ResponseEntity<List<ParticipantResponse>> findParticipants(@PathVariable final Long id) {
    final List<ParticipantResponse> responses = eventService.findParticipants(id);
    return ResponseEntity.ok(responses);
  }

  @GetMapping
  public ResponseEntity<List<EventResponse>> findEvents(@RequestParam final int year,
      @RequestParam final int month, @RequestParam(required = false) final String tag,
      @RequestParam(required = false) final String status) {
    return ResponseEntity.ok(eventService.findEvents(LocalDate.now(), year, month, tag, status));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EventDetailResponse addEvent(
      @RequestBody @Valid final EventDetailRequest request) {
    return eventService.addEvent(request);
  }
}
