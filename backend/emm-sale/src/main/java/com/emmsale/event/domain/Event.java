package com.emmsale.event.domain;

import static lombok.AccessLevel.PROTECTED;

import com.emmsale.base.BaseEntity;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.member.domain.Member;
import com.emmsale.tag.domain.Tag;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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

  @Embedded
  private EventPeriod eventPeriod;

  @Column(nullable = false)
  private String informationUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EventType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentType paymentType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EventMode eventMode;

  private String imageUrl;

  @Column(nullable = false)
  private String organization;

  @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  private List<EventTag> tags = new ArrayList<>();

  @OneToMany(mappedBy = "event", cascade = CascadeType.PERSIST)
  private List<RecruitmentPost> recruitmentPosts = new ArrayList<>();

  public Event(
      final String name,
      final String location,
      final LocalDateTime startDate,
      final LocalDateTime endDate,
      final LocalDateTime applyStartDate,
      final LocalDateTime applyEndDate,
      final String informationUrl,
      final EventType eventType,
      final String imageUrl,
      final PaymentType paymentType,
      final EventMode eventMode,
      final String organization
  ) {
    this.name = name;
    this.location = location;
    this.eventPeriod = new EventPeriod(startDate, endDate, applyStartDate, applyEndDate);
    this.informationUrl = informationUrl;
    this.type = eventType;
    this.imageUrl = imageUrl;
    this.paymentType = paymentType;
    this.eventMode = eventMode;
    this.organization = organization;
  }

  public RecruitmentPost createRecruitmentPost(final Member member, final String content) {
    final RecruitmentPost recruitmentPost = new RecruitmentPost(member, this, content);
    recruitmentPosts.add(recruitmentPost);
    return recruitmentPost;
  }

  public void addAllEventTags(final List<Tag> tags) {
    final List<EventTag> eventTags = tags.stream()
        .map(tag -> new EventTag(this, tag))
        .collect(Collectors.toList());
    this.tags.addAll(eventTags);
  }

  public void validateAlreadyCreateRecruitmentPost(final Member member) {
    if (isAlreadyCreateRecruitmentPost(member)) {
      throw new EventException(EventExceptionType.ALREADY_CREATE_RECRUITMENT_POST);
    }
  }

  private boolean isAlreadyCreateRecruitmentPost(final Member member) {
    return recruitmentPosts.stream()
        .anyMatch(post -> post.isSameMember(member));
  }

  public Event updateEventContent(
      final String name,
      final String location,
      final LocalDateTime startDate,
      final LocalDateTime endDate,
      final LocalDateTime applyStartDate,
      final LocalDateTime applyEndDate,
      final String informationUrl,
      final List<Tag> tags,
      final EventType type,
      final EventMode eventMode,
      final PaymentType paymentType,
      final String organization
  ) {
    this.name = name;
    this.location = location;
    this.eventPeriod = new EventPeriod(startDate, endDate, applyStartDate, applyEndDate);
    this.informationUrl = informationUrl;
    this.tags = new ArrayList<>();
    this.type = type;
    this.eventMode = eventMode;
    this.paymentType = paymentType;
    this.organization = organization;

    addAllEventTags(tags);

    return this;
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
