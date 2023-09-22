package com.emmsale.message_room.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MessageSendResponse {

  private final String roomId;
}
