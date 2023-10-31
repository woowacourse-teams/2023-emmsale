package com.emmsale.scrap.api;

import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.member.domain.Member;
import com.emmsale.scrap.application.ScrapCommandService;
import com.emmsale.scrap.application.ScrapQueryService;
import com.emmsale.scrap.application.dto.ScrapRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scraps")
@RequiredArgsConstructor
public class ScrapApi {

  private final ScrapQueryService scrapQueryService;
  private final ScrapCommandService scrapCommandService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<EventResponse> findAllScraps(final Member member) {
    return scrapQueryService.findAllScraps(member);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void append(final Member member, @RequestBody final ScrapRequest scrapRequest) {
    scrapCommandService.append(member, scrapRequest);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(final Member member, @RequestParam("event-id") final Long eventId) {
    scrapCommandService.deleteScrap(member, eventId);
  }

}
