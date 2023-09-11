package com.emmsale.fixture;

import com.emmsale.message_room.domain.Message;
import java.time.LocalDateTime;

public class MessageFixture {

  public static Message create(
      final String content,
      final Long senderId,
      final String roomUUID,
      final LocalDateTime createdAt) {
    return new Message(
        content,
        senderId,
        roomUUID,
        createdAt
    );
  }
}
