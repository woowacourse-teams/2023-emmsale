package com.emmsale.scrap.domain;

import com.emmsale.base.BaseEntity;
import com.emmsale.event.domain.Event;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long memberId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Event event;

  public Scrap(final Long memberId, final Event event) {
    this.memberId = memberId;
    this.event = event;
  }

  public boolean isNotOwner(final Long memberId) {
    return !this.memberId.equals(memberId);
  }
}
