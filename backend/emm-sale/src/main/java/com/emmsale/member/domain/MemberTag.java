package com.emmsale.member.domain;

import com.emmsale.base.BaseEntity;
import com.emmsale.tag.domain.Tag;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class MemberTag extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Member member;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Tag tag;
}
