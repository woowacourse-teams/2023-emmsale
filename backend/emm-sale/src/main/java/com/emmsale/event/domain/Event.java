package com.emmsale.event.domain;

import static lombok.AccessLevel.PROTECTED;

import com.emmsale.base.BaseEntity;
import com.emmsale.comment.domain.Comment;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.member.domain.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
  private String informationUrl;
  @OneToMany(mappedBy = "event")
  private List<EventTag> tags;
  @OneToMany(mappedBy = "event")
  private List<Comment> comments;
  @OneToMany(mappedBy = "event",cascade = CascadeType.ALL)
  private List<Participant> participants = new ArrayList<>();

  public Event(
      final String name,
      final String location,
      final LocalDateTime startDate,
      final LocalDateTime endDate,
      final String informationUrl
  ) {
    this.name = name;
    this.location = location;
    this.startDate = startDate;
    this.endDate = endDate;
    this.informationUrl = informationUrl;
  }

  public Participant addParticipant(final Member member) {
    final Participant participant = new Participant(member, this);
    participants.add(participant);
    return participant;
  }

  public void validateAlreadyParticipate(final Member member) {
    if (isAlreadyParticipate(member)) {
      throw new EventException(EventExceptionType.ALREADY_PARTICIPATED);
    }
  }

  private boolean isAlreadyParticipate(final Member member) {
    return participants.stream()
        .map(participant -> participant.isSameMember(member))
        .findAny()
        .isPresent();
  }
}
