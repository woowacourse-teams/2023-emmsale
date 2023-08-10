package com.emmsale.event.domain;

import static lombok.AccessLevel.PROTECTED;

import com.emmsale.base.BaseEntity;
import com.emmsale.comment.domain.Comment;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.member.domain.Member;
import com.emmsale.tag.domain.Tag;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Event extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private String location;
  @Column(nullable = false)
  private LocalDateTime startDate;
  @Column(nullable = false)
  private LocalDateTime endDate;
  @Column(nullable = false)
  private LocalDateTime applyStartDate;
  @Column(nullable = false)
  private LocalDateTime applyEndDate;
  @Column(nullable = false)
  private String informationUrl;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EventType type;
  private String imageUrl;
  @OneToMany(mappedBy = "event", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
  private List<EventTag> tags = new ArrayList<>();
  @OneToMany(mappedBy = "event")
  private List<Comment> comments;
  @OneToMany(mappedBy = "event", cascade = CascadeType.PERSIST)
  private List<Participant> participants = new ArrayList<>();

  public Event(
      final String name,
      final String location,
      final LocalDateTime startDate,
      final LocalDateTime endDate,
      final LocalDateTime applyStartDate,
      final LocalDateTime applyEndDate,
      final String informationUrl,
      final EventType eventType,
      final String imageUrl
  ) {
    validateStartBeforeOrEqualEndDateTime(startDate, endDate);
    validateApplyDateTimes(applyStartDate, applyEndDate, startDate, endDate);

    this.name = name;
    this.location = location;
    this.startDate = startDate;
    this.endDate = endDate;
    this.applyStartDate = applyStartDate;
    this.applyEndDate = applyEndDate;
    this.informationUrl = informationUrl;
    this.type = eventType;
    this.imageUrl = imageUrl;
  }

  public Participant addParticipant(final Member member, final String content) {
    final Participant participant = new Participant(member, this, content);
    participants.add(participant);
    return participant;
  }

  public void addAllEventTags(final List<Tag> tags) {
    final List<EventTag> eventTags = tags.stream()
        .map(tag -> new EventTag(this, tag))
        .collect(Collectors.toList());
    this.tags.addAll(eventTags);
  }

  public void validateAlreadyParticipate(final Member member) {
    if (isAlreadyParticipate(member)) {
      throw new EventException(EventExceptionType.ALREADY_PARTICIPATED);
    }
  }

  private boolean isAlreadyParticipate(final Member member) {
    return participants.stream()
        .anyMatch(participant -> participant.isSameMember(member));
  }

  public EventStatus calculateEventStatus(final LocalDate now) {
    if (now.isBefore(startDate.toLocalDate())) {
      return EventStatus.UPCOMING;
    }
    if (now.isAfter(endDate.toLocalDate())) {
      return EventStatus.ENDED;
    }
    return EventStatus.IN_PROGRESS;
  }

  public EventStatus calculateEventApplyStatus(final LocalDate now) {
    if (now.isBefore(applyStartDate.toLocalDate())) {
      return EventStatus.UPCOMING;
    }
    if (now.isAfter(applyEndDate.toLocalDate())) {
      return EventStatus.ENDED;
    }
    return EventStatus.IN_PROGRESS;
  }

  public Event updateEventContent(
      final String name,
      final String location,
      final LocalDateTime startDate,
      final LocalDateTime endDate,
      final LocalDateTime applyStartDate,
      final LocalDateTime applyEndDate,
      final String informationUrl,
      final List<Tag> tags
  ) {
    validateStartBeforeOrEqualEndDateTime(startDate, endDate);
    validateApplyDateTimes(applyStartDate, applyEndDate, startDate, endDate);

    this.name = name;
    this.location = location;
    this.startDate = startDate;
    this.endDate = endDate;
    this.applyStartDate = applyStartDate;
    this.applyEndDate = applyEndDate;
    this.informationUrl = informationUrl;
    this.tags = new ArrayList<>();

    addAllEventTags(tags);

    return this;
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

  public int calculateRemainingDays(final LocalDate today) {
    return Period.between(today, startDate.toLocalDate()).getDays();
  }

  public boolean isDiffer(final Long eventId) {
    return !this.getId().equals(eventId);
  }

  public List<String> extractTags() {
    return tags.stream()
        .map(tag -> tag.getTag().getName())
        .collect(Collectors.toList());
  }
}
