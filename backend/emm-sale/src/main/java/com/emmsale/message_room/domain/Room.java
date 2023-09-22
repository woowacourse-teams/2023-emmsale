package com.emmsale.message_room.domain;

import java.time.LocalDateTime;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Room {

  @EmbeddedId
  private RoomId roomId;

  private LocalDateTime lastExitedTime;

  public Room(final RoomId roomId, final LocalDateTime lastExitedTime) {
    this.roomId = roomId;
    this.lastExitedTime = lastExitedTime;
  }

  public boolean isInterlocutorWith(final Long loginMemberId) {
    return roomId.isInterlocutors(loginMemberId);
  }
}
