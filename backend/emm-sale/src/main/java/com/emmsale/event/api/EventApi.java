package com.emmsale.event.api;

import com.emmsale.event.application.EventService;
import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventType;
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
}