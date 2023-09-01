package com.emmsale.message_room.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MessageSendRequest {

  private final Long senderId;
  private final Long receiverId;
  private final String content;
}
