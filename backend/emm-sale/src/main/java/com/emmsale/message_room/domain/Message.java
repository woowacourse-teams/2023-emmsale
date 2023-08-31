package com.emmsale.message_room.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private Room room;

  private LocalDateTime createdAt;

  public Message(
      final String content,
      final Long senderId,
      final Room room
  ) {
    this.content = content;
    this.senderId = senderId;
    this.room = room;
    this.createdAt = LocalDateTime.now();
  }
}
