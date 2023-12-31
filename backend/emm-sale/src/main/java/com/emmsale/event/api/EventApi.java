package com.emmsale.event.api;

import com.emmsale.event.application.EventCommandService;
import com.emmsale.event.application.EventQueryService;
import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventApi {

  private final EventQueryService eventQueryService;
  private final EventCommandService eventCommandService;

  @GetMapping("/{id}")
  public ResponseEntity<EventResponse> findEventById(@PathVariable final Long id) {
    return ResponseEntity.ok(eventQueryService.findEvent(id, LocalDate.now()));
  }

  @GetMapping
  public ResponseEntity<List<EventResponse>> findEvents(
      @RequestParam(required = false) final EventType category,
      @RequestParam(name = "start_date", required = false) final String startDate,
      @RequestParam(name = "end_date", required = false) final String endDate,
      @RequestParam(required = false) final List<String> tags,
      @RequestParam(required = false) final List<EventStatus> statuses,
      @RequestParam(required = false) final String keyword) {
    return ResponseEntity.ok(
        eventQueryService.findEvents(category, LocalDateTime.now(), startDate, endDate, tags,
            statuses,
            keyword));
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public EventResponse addEvent(@RequestPart @Valid final EventDetailRequest request,
      @RequestPart final List<MultipartFile> images) {
    return eventCommandService.addEvent(request, images);
  }

  @PutMapping(path = "/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public EventResponse updateEvent(@PathVariable final Long eventId,
      @RequestPart @Valid final EventDetailRequest request,
      @RequestPart final List<MultipartFile> images) {
    return eventCommandService.updateEvent(eventId, request, images);
  }

  @DeleteMapping("/{eventId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteEvent(@PathVariable final Long eventId) {
    eventCommandService.deleteEvent(eventId);
  }
}
