package com.emmsale.event.application.dto;

import com.emmsale.event.domain.Event;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EventDetailResponse {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd hh:mm");
  private final Long id;
  private final String name;
  private final String informationUrl;
  private final String startDate;
  private final String endDate;
  private final String location;

  public static EventDetailResponse from(final Event event) {
    return new EventDetailResponse(
        event.getId(),
        event.getName(),
        event.getInformationUrl(),
        event.getStartDate().format(DATE_TIME_FORMATTER),
        event.getEndDate().format(DATE_TIME_FORMATTER),
        event.getLocation()
    );
  }
}
