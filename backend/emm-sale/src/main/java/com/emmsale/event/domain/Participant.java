package com.emmsale.event.domain;

import com.emmsale.base.BaseEntity;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.member.domain.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "event_member")
public class Participant extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Event event;

  @Column(nullable = false)
  private String content;

  public Participant(final Member member, final Event event, final String content) {
    event.validateAlreadyParticipate(member);
    this.member = member;
    this.event = event;
    this.content = content;
  }

  public boolean isSameMember(final Member member) {
    return this.member.isMe(member);
  }

  public void updateContent(final String content) {
    this.content = content;
  }

  public void validateEvent(final Long eventId) {
    if (event.isDiffer(eventId)) {
      throw new EventException(EventExceptionType.PARTICIPANT_NOT_BELONG_EVENT);
    }
  }

  public void validateOwner(final Member member) {
    if (this.member.isNotMe(member)) {
      throw new EventException(EventExceptionType.FORBIDDEN_UPDATE_PARTICIPATE);
    }
  }
}
