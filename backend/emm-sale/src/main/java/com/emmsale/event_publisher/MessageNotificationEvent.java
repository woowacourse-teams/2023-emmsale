package com.emmsale.event_publisher;

import com.emmsale.message_room.domain.Message;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MessageNotificationEvent {

  private final String roomId;
  private final String content;
  private final Long senderId;
  private final Long receiverId;
  private final LocalDateTime messageCreatedAt;

  public static MessageNotificationEvent of(final Message message, final Long receiverId) {
    return new MessageNotificationEvent(
        message.getRoomId(),
        message.getContent(),
        message.getSender().getId(),
        receiverId,
        message.getCreatedAt()
    );
  }
}
