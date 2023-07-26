package com.emmsale.event.application.dto;

import com.emmsale.event.domain.Event;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EventDetailResponse {

  private static final String EXPECTED = "예정";
  private static final String IN_PROGRESS = "진행 중";
  private static final String END = "종료";

  private final Long id;
  private final String name;
  private final String informationUrl;
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private final LocalDateTime startDate;
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private final LocalDateTime endDate;
  private final String location;
  private final String status;

  public static EventDetailResponse from(final Event event) {
    return new EventDetailResponse(
        event.getId(),
        event.getName(),
        event.getInformationUrl(),
        event.getStartDate(),
        event.getEndDate(),
        event.getLocation(),
        calculateStatus(event.getStartDate(), event.getEndDate())
    );
  }

  private static String calculateStatus(
      final LocalDateTime startDate,
      final LocalDateTime endDate
  ) {
    final LocalDateTime now = LocalDateTime.now();
    if (startDate.isBefore(now)) {
      return EXPECTED;
    } else if (endDate.isBefore(now)) {
      return IN_PROGRESS;
    }
    return END;
  }
}
