package com.emmsale.message_room.application.dto;

import com.emmsale.message_room.domain.Message;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MessageResponse {

  private final Long senderId;
  private final String content;
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private final LocalDateTime createdAt;

  public static MessageResponse from(final Message message) {
    return new MessageResponse(
        message.getSenderId(),
        message.getContent(),
        message.getCreatedAt()
    );
  }
}
