package com.emmsale.scrap.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScrapRequest {

  private final Long eventId;

  private ScrapRequest() {
    this(null);
  }
}
