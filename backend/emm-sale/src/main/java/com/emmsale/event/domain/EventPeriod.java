package com.emmsale.event.domain;

import static lombok.AccessLevel.PROTECTED;

import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class EventPeriod {

  @Column(nullable = false)
  private LocalDateTime startDate;
  @Column(nullable = false)
  private LocalDateTime endDate;
  @Column(nullable = false)
  private LocalDateTime applyStartDate;
  @Column(nullable = false)
  private LocalDateTime applyEndDate;

  public EventPeriod(final LocalDateTime startDate, final LocalDateTime endDate,
      final LocalDateTime applyStartDate, final LocalDateTime applyEndDate) {
    validateStartBeforeOrEqualEndDateTime(startDate, endDate);
    validateApplyDateTimes(applyStartDate, applyEndDate, startDate, endDate);
    this.startDate = startDate;
    this.endDate = endDate;
    this.applyStartDate = applyStartDate;
    this.applyEndDate = applyEndDate;
  }

  private void validateStartBeforeOrEqualEndDateTime(final LocalDateTime startDateTime,
      final LocalDateTime endDateTime) {
    if (startDateTime.isAfter(endDateTime)) {
      throw new EventException(EventExceptionType.START_DATE_TIME_AFTER_END_DATE_TIME);
    }
  }

  private void validateApplyDateTimes(final LocalDateTime applyStartDateTime,
      final LocalDateTime applyEndDateTime, final LocalDateTime startDateTime,
      final LocalDateTime endDateTime) {
    if (applyStartDateTime.isAfter(applyEndDateTime)) {
      throw new EventException(EventExceptionType.SUBSCRIPTION_START_AFTER_SUBSCRIPTION_END);
    }
    if (applyEndDateTime.isAfter(endDateTime)) {
      throw new EventException(EventExceptionType.SUBSCRIPTION_END_AFTER_EVENT_END);
    }
    if (applyStartDateTime.isAfter(startDateTime)) {
      throw new EventException(EventExceptionType.SUBSCRIPTION_START_AFTER_EVENT_START);
    }
  }

  public EventStatus calculateEventStatus(final LocalDateTime now) {
    if (now.isBefore(startDate)) {
      return EventStatus.UPCOMING;
    }
    if (now.isAfter(endDate)) {
      return EventStatus.ENDED;
    }
    return EventStatus.IN_PROGRESS;
  }

  @Deprecated
  public int calculateRemainingDays(final LocalDate today) {
    return java.time.Period.between(today, startDate.toLocalDate()).getDays();
  }

  @Deprecated
  public EventStatus calculateEventApplyStatus(final LocalDateTime now) {
    if (now.isBefore(applyStartDate)) {
      return EventStatus.UPCOMING;
    }
    if (now.isAfter(applyEndDate)) {
      return EventStatus.ENDED;
    }
    return EventStatus.IN_PROGRESS;
  }

  @Deprecated
  public int calculateApplyRemainingDays(final LocalDate today) {
    return java.time.Period.between(today, applyStartDate.toLocalDate()).getDays();
  }
}
