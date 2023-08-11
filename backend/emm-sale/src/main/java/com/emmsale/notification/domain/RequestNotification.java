package com.emmsale.notification.domain;

import com.emmsale.base.BaseEntity;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "request_notification")
public class RequestNotification extends BaseEntity {

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
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RequestNotificationStatus status;

  public RequestNotification(
      final Long senderId,
      final Long receiverId,
      final Long eventId,
      final String message
  ) {
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.eventId = eventId;
    this.message = message;
    this.status = RequestNotificationStatus.IN_PROGRESS;
  }

  public void modifyStatus(final RequestNotificationStatus status) {
    this.status = status;
  }

  public boolean isNotOwner(final Long memberId) {
    return !receiverId.equals(memberId);
  }
}
