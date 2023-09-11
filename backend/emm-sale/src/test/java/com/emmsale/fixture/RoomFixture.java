package com.emmsale.fixture;

import com.emmsale.message_room.domain.Room;
import com.emmsale.message_room.domain.RoomId;
import java.time.LocalDateTime;

public class RoomFixture {

  public static Room create(final String uuid, Long memberId, LocalDateTime lastExitedTime) {
    return new Room(new RoomId(uuid, memberId), lastExitedTime);
  }
}
