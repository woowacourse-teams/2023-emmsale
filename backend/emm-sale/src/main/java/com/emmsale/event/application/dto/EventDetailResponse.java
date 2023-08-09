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
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private final LocalDateTime subscriptionStartDate;
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private final LocalDateTime subscriptionEndDate;
  private final String location;
  private final String status;
  private final String subscriptionStatus;
  private final List<String> tags;
  private final String imageUrl;
  private final Integer remainingDays;
  private final String type;

  public static EventDetailResponse from(final Event event, final LocalDate today) {
    final List<String> tagNames = event.getTags().stream()
        .map(EventTag::getTag)
        .map(Tag::getName)
        .collect(toUnmodifiableList());

    return new EventDetailResponse(
        event.getId(),
        event.getName(),
        event.getInformationUrl(),
        event.getStartDate(),
        event.getEndDate(),
        event.getSubscriptionStartDate(),
        event.getSubscriptionEndDate(),
        event.getLocation(),
        event.calculateEventStatus(today).name(),
        event.calculateEventSubscriptionStatus(today).name(),
        tagNames,
        event.getImageUrl(),
        event.calculateRemainingDays(today),
        event.getType().toString()
    );
  }
}
