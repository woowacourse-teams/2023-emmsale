package com.emmsale.message_room.domain;

import com.emmsale.member.domain.Member;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
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

  @ManyToOne
  @JoinColumn(nullable = false)
  private Member sender;

  @Column(nullable = false)
  private String roomId;

  private LocalDateTime createdAt;

  public Message(
      final Long id,
      final String content,
      final Member sender,
      final String roomId,
      final LocalDateTime createdAt
  ) {
    this.id = id;
    this.content = content;
    this.sender = sender;
    this.roomId = roomId;
    this.createdAt = createdAt;
  }

  public Message(
      final String content,
      final Member sender,
      final String roomId,
      final LocalDateTime createdAt
  ) {
    this(null, content, sender, roomId, createdAt);
  }
}
