package com.emmsale.event.api;

import com.emmsale.event.application.EventQueryService;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventApi {

  private final EventQueryService eventQueryService;

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
}
