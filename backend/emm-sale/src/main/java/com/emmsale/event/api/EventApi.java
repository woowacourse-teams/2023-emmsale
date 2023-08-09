package com.emmsale.event.api;

import static java.lang.String.format;
import static java.net.URI.create;

import com.emmsale.event.application.EventService;
import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.EventParticipateRequest;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.application.dto.ParticipantResponse;
import com.emmsale.event.application.dto.ParticipateUpdateRequest;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventType;
import com.emmsale.member.domain.Member;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    return ResponseEntity.ok(eventService.findEvent(id, LocalDate.now()));
  }

  @PostMapping("/{eventId}/participants")
  public ResponseEntity<Void> participateEvent(
      @PathVariable final Long eventId,
      @RequestBody @Valid final EventParticipateRequest request,
      final Member member
  ) {
    final Long participantId = eventService.participate(eventId, request, member);

    return ResponseEntity
        .created(create(format("/events/%s/participants/%s", eventId, participantId)))
        .build();
  }

  @PutMapping("/{eventId}/participants/{participantId}")
  public ResponseEntity<Void> updateParticipate(
      @PathVariable final Long eventId,
      @PathVariable final Long participantId,
      @RequestBody @Valid final ParticipateUpdateRequest request,
      final Member member
  ) {
    eventService.updateParticipant(eventId, participantId, request, member);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{eventId}/participants")
  public ResponseEntity<String> cancelParticipateEvent(
      @PathVariable final Long eventId,
      @RequestParam(name = "member-id") final Long memberId,
      final Member member
  ) {
    eventService.cancelParticipate(eventId, memberId, member);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/participants")
  public ResponseEntity<List<ParticipantResponse>> findParticipants(@PathVariable final Long id) {
    final List<ParticipantResponse> responses = eventService.findParticipants(id);
    return ResponseEntity.ok(responses);
  }

  @GetMapping
  public ResponseEntity<List<EventResponse>> findEvents(
      @RequestParam final EventType category,
      @RequestParam(name = "start_date", required = false) final String startDate,
      @RequestParam(name = "end_date", required = false) final String endDate,
      @RequestParam(required = false) final List<String> tags,
      @RequestParam(required = false) final List<EventStatus> statuses) {
    return ResponseEntity.ok(
        eventService.findEvents(category, LocalDate.now(), startDate, endDate, tags, statuses));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EventDetailResponse addEvent(
      @RequestBody @Valid final EventDetailRequest request) {
    return eventService.addEvent(request, LocalDate.now());
  }

  @PutMapping("/{eventId}")
  @ResponseStatus(HttpStatus.OK)
  public EventDetailResponse updateEvent(@PathVariable final Long eventId,
      @RequestBody @Valid final EventDetailRequest request) {
    return eventService.updateEvent(eventId, request, LocalDate.now());
  }

  @DeleteMapping("/{eventId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteEvent(@PathVariable final Long eventId) {
    eventService.deleteEvent(eventId);
  }

  @GetMapping("/{eventId}/participants/already-participate")
  public ResponseEntity<Boolean> isAlreadyParticipate(
      @PathVariable final Long eventId,
      @RequestParam(name = "member-id") final Long memberId
  ) {
    return ResponseEntity.ok(eventService.isAlreadyParticipate(eventId, memberId));
  }
}
