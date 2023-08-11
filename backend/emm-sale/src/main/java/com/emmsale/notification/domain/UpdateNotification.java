package com.emmsale.notification.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "update_notification")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateNotification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private Long receiverId;
  @Column(nullable = false)
  private Long redirectId;
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private UpdateNotificationType updateNotificationType;
  private LocalDateTime createdAt;

  public UpdateNotification(
      final Long receiverId,
      final Long redirectId,
      final UpdateNotificationType updateNotificationType,
      final LocalDateTime createdAt
  ) {
    this.receiverId = receiverId;
    this.redirectId = redirectId;
    this.updateNotificationType = updateNotificationType;
    this.createdAt = createdAt;
  }
}
