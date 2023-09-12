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
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private Long senderId;

  @Column(nullable = false)
  private String roomId;

  private LocalDateTime createdAt;

  public Message(
      final String content,
      final Long senderId,
      final String roomId,
      final LocalDateTime createdAt
  ) {
    this.content = content;
    this.senderId = senderId;
    this.roomId = roomId;
    this.createdAt = createdAt;
  }
}
