package com.emmsale.member.domain;

import com.emmsale.base.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true, nullable = false)
  private Long githubId;
  @Column(nullable = false)
  private String name;
  @Column
  private String description;
  @Column
  private String openProfileUrl;
  @Column(nullable = false)
  private String imageUrl;

  public Member(final Long id, final Long githubId, final String imageUrl, final String name) {
    this.id = id;
    this.githubId = githubId;
    this.imageUrl = imageUrl;
    this.name = name;
  }

  public Member(final Long githubId, final String imageUrl, final String name) {
    this.githubId = githubId;
    this.imageUrl = imageUrl;
    this.name = name;
  }

  public void updateName(final String name) {
    this.name = name;
  }

  public boolean isMe(final Member member) {
    return this.id.equals(member.id);
  }

  public boolean isNotMe(final Long id) {
    return !this.id.equals(id);
  }
}
