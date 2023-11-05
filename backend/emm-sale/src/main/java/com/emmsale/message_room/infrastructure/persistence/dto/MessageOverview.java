package com.emmsale.message_room.infrastructure.persistence.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MessageOverview {

  private final Long id;
  private final String content;
  private final Long senderId;
  private final LocalDateTime createdAt;
  private final String roomUUID;
}
