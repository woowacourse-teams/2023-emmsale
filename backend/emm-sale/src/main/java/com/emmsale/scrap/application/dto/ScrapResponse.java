package com.emmsale.scrap.application.dto;

import com.emmsale.event.domain.Event;
import com.emmsale.scrap.domain.Scrap;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class ScrapResponse {

  private final Long scrapId;
  private final Long eventId;
  private final String name;
  private final String status;
  private final String imageUrl;
  private final List<String> tags;

  public static ScrapResponse from(final Scrap scrap) {
    final Event event = scrap.getEvent();
    final String eventStatus = event.getEventPeriod().calculateEventStatus(LocalDate.now())
        .toString();
    final List<String> eventTags = event.getTags().stream()
        .map(eventTag -> eventTag.getTag().getName())
        .collect(Collectors.toList());

    return new ScrapResponse(
        scrap.getId(),
        event.getId(),
        event.getName(),
        eventStatus,
        event.getImageUrl(),
        eventTags
    );
  }

}
