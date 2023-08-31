package com.emmsale.feed.domain;

import com.emmsale.base.BaseEntity;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private Long eventId;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Member writer;
  @Column(nullable = false, length = 50)
  private String title;
  @Column(nullable = false, length = 1000)
  private String content;
  // TODO: 2023/08/31 이미지 추가

  public Feed(final Long eventId, final Member writer, final String title, final String content) {
    this.eventId = eventId;
    this.writer = writer;
    this.title = title;
    this.content = content;
  }

  public void updateFeed(final Long eventId, final String title, final String content) {
    this.eventId = eventId;
    this.title = title;
    this.content = content;
  }

  public boolean isNotOwner(final Long memberId) {
    return !this.writer.getId().equals(memberId);
  }
}
