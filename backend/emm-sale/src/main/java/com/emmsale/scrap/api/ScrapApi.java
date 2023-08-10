package com.emmsale.scrap.api;

import com.emmsale.member.domain.Member;
import com.emmsale.scrap.application.ScrapCommandService;
import com.emmsale.scrap.application.dto.ScrapRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scraps")
@RequiredArgsConstructor
public class ScrapApi {

  private final ScrapCommandService scrapCommandService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void append(final Member member, @RequestBody final ScrapRequest scrapRequest) {
    scrapCommandService.append(member, scrapRequest);
  }

  @DeleteMapping("/{scrapId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(final Member member, @PathVariable final Long scrapId) {
    scrapCommandService.deleteScrap(member, scrapId);
  }

}


