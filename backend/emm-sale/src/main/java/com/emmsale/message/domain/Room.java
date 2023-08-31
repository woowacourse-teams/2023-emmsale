package com.emmsale.message.domain;

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

  @Column(nullable = false, name = "member1_id")
  private Long member1Id;

  @Column(nullable = false, name = "member1_last_exited_time")
  private LocalDateTime member1LastExitedTime;

  @Column(nullable = false, name = "member2_id")
  private Long member2Id;

  @Column(nullable = false, name = "member2_last_exited_time")
  private LocalDateTime member2LastExitedTime;

  public Room(
      final Long member1Id,
      final LocalDateTime member1LastExitedTime,
      final Long member2Id,
      final LocalDateTime member2LastExitedTime
  ) {
    this.member1Id = member1Id;
    this.member1LastExitedTime = member1LastExitedTime;
    this.member2Id = member2Id;
    this.member2LastExitedTime = member2LastExitedTime;
  }
}
