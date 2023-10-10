package com.emmsale.notification.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private NotificationType type;

  @Column(nullable = false)
  private Long receiverId;

  @Column(nullable = false)
  private Long redirectId;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String jsonData;

  private boolean isRead;

  public Notification(
      final NotificationType type, final Long receiverId,
      final Long redirectId, final LocalDateTime createdAt,
      final String jsonData
  ) {
    this.type = type;
    this.receiverId = receiverId;
    this.redirectId = redirectId;
    this.createdAt = createdAt;
    this.jsonData = jsonData;
    this.isRead = false;
  }

  public boolean isOwner(final Long memberId) {
    return receiverId.equals(memberId);
  }

  public void read() {
    isRead = true;
  }
}
