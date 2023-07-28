package com.emmsale.notification.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FcmToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String token;
  @Column(nullable = false, unique = true)
  private Long memberId;

  public FcmToken(final String token, final Long memberId) {
    this.token = token;
    this.memberId = memberId;
  }

  public void update(final String token) {
    this.token = token;
  }
}
