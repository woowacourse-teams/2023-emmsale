package com.emmsale.event.application.dto;

import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventTag;
import com.emmsale.tag.domain.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EventDetailResponse {

  public static final String DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss";

  private final Long id;
  private final String name;
  private final String informationUrl;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime startDate;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime endDate;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime applyStartDate;
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDateTime applyEndDate;
  private final String location;
  private final String status;
  private final String applyStatus;
  private final List<String> tags;
  private final String imageUrl;
  private final Integer remainingDays;
  private final Integer applyRemainingDays;
  private final String type;
  private final List<String> imageUrls;
  private final String organization;
  private final String paymentType;

  public static EventDetailResponse from(
      final Event event,
      final LocalDate today,
      final String imageUrl,
      final List<String> imageUrls
  ) {
    final List<String> tagNames = event.getTags().stream()
        .map(EventTag::getTag)
        .map(Tag::getName)
        .collect(toUnmodifiableList());

    return new EventDetailResponse(
        event.getId(),
        event.getName(),
        event.getInformationUrl(),
        event.getEventPeriod().getStartDate(),
        event.getEventPeriod().getEndDate(),
        event.getEventPeriod().getApplyStartDate(),
        event.getEventPeriod().getApplyEndDate(),
        event.getLocation(),
        event.getEventPeriod().calculateEventStatus(today).name(),
        event.getEventPeriod().calculateEventApplyStatus(today).name(),
        tagNames,
        imageUrl,
        event.getEventPeriod().calculateRemainingDays(today),
        event.getEventPeriod().calculateApplyRemainingDays(today),
        event.getType().toString(),
        imageUrls,
        event.getOrganization(),
        event.getPaymentType().getValue()
    );
  }
}
