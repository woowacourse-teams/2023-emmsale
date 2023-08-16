package com.emmsale.member.domain;

import com.emmsale.base.BaseEntity;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import java.util.Optional;
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

  private static final int MAX_DESCRIPTION_LENGTH = 100;
  private static final String DEFAULT_DESCRIPTION = "";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true, nullable = false)
  private Long githubId;
  private String name;
  @Column(nullable = false)
  private String description;
  @Column
  @Getter(value = AccessLevel.PRIVATE)
  private String openProfileUrl;
  @Column(nullable = false)
  private String imageUrl;
  @Column(nullable = false)
  private String githubUsername;


  public Member(final Long id, final Long githubId, final String imageUrl, final String name,
      final String githubUsername) {
    this.id = id;
    this.githubId = githubId;
    this.imageUrl = imageUrl;
    this.name = name;
    this.description = DEFAULT_DESCRIPTION;
    this.githubUsername = githubUsername;
  }

  public Member(final Long githubId, final String imageUrl, final String githubUsername) {
    this.githubId = githubId;
    this.imageUrl = imageUrl;
    this.description = DEFAULT_DESCRIPTION;
    this.githubUsername = githubUsername;
  }

  public void updateName(final String name) {
    this.name = name;
  }

  public void updateOpenProfileUrl(final String openProfileUrl) {
    this.openProfileUrl = openProfileUrl;
  }

  public void updateDescription(final String description) {
    validateDescriptionNull(description);
    validateDescriptionLength(description);
    if (description.isBlank()) {
      this.description = DEFAULT_DESCRIPTION;
      return;
    }
    this.description = description;
  }

  private void validateDescriptionNull(final String description) {
    if (description == null) {
      throw new MemberException(MemberExceptionType.NULL_DESCRIPTION);
    }
  }

  private void validateDescriptionLength(final String description) {
    if (description.length() > MAX_DESCRIPTION_LENGTH) {
      throw new MemberException(MemberExceptionType.OVER_LENGTH_DESCRIPTION);
    }
  }

  public boolean isMe(final Member member) {
    return isMe(member.getId());
  }

  public boolean isMe(final Long id) {
    return this.id.equals((id));
  }

  public boolean isNotMe(final Member member) {
    return isNotMe(member.getId());
  }


  public boolean isNotMe(final Long id) {
    return !this.id.equals(id);
  }

  public boolean isOnboarded() {
    return name != null;
  }

  public Optional<String> getOptionalOpenProfileUrl() {
    return Optional.ofNullable(openProfileUrl);
  }
}
