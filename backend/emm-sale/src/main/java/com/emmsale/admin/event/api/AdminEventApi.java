package com.emmsale.admin.event.api;

import com.emmsale.admin.event.application.EventCommandService;
import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.application.dto.EventResponse;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventApi {

  private final EventCommandService eventCommandService;

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
