package com.emmsale.notification.domain;

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
public class Notification extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private Long senderId;
  @Column(nullable = false)
  private Long receiverId;
  @Column(nullable = false)
  private Long eventId;
  @Column(nullable = false)
  private String message;

  public Notification(
      final Long senderId,
      final Long receiverId,
      final Long eventId,
      final String message
  ) {
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.eventId = eventId;
    this.message = message;
  }
}
