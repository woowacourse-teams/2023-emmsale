package com.emmsale.event.api;

import com.emmsale.event.application.EventService;
import com.emmsale.event.application.dto.EventResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventApi {

  private final EventService eventService;

  @GetMapping
  public ResponseEntity<List<EventResponse>> findEvents(@RequestParam final int year,
      @RequestParam final int month, @RequestParam(required = false) final String tag,
      @RequestParam(required = false) final String status) {

    return ResponseEntity.ok(eventService.findEvents(LocalDate.now(), year, month, tag, status));
  }

}
