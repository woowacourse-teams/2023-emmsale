package com.emmsale.comment.domain;

import com.emmsale.base.BaseEntity;
import com.emmsale.event.domain.Event;
import com.emmsale.member.domain.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Event event;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private Comment parent;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Member member;
  @Column(nullable = false)
  private String content;
  private boolean isDeleted;

  public Comment(
      final Event event,
      final Comment parent,
      final Member member,
      final String content
  ) {
    this.event = event;
    this.parent = parent;
    this.member = member;
    this.content = content;
  }

  public Long getId() {
    return id;
  }

  public Event getEvent() {
    return event;
  }

  public Comment getParent() {
    return parent;
  }

  public Member getMember() {
    return member;
  }

  public String getContent() {
    return content;
  }

  public boolean isDeleted() {
    return isDeleted;
  }
}
