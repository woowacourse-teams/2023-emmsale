package com.emmsale.message_room.domain;

import java.time.LocalDateTime;
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
public class Room {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long requesterId;

  @Column(nullable = false)
  private LocalDateTime requesterLastExitedTime;

  @Column(nullable = false)
  private Long receiverId;

  @Column(nullable = false)
  private LocalDateTime receiverLastExitedTime;

  public Room(
      final Long requesterId,
      final LocalDateTime requesterLastExitedTime,
      final Long receiverId,
      final LocalDateTime receiverLastExitedTime
  ) {
    this.requesterId = requesterId;
    this.requesterLastExitedTime = requesterLastExitedTime;
    this.receiverId = receiverId;
    this.receiverLastExitedTime = receiverLastExitedTime;
  }
}
