package com.emmsale.message_room.application.dto;

import com.emmsale.member.domain.Member;
import com.emmsale.message_room.infrastructure.persistence.dto.MessageOverview;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RoomResponse {

  private final String roomId;
  private final Long interlocutorId;
  private final String interlocutorName;
  private final String interlocutorProfile;
  private final String recentlyMessage;
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private final LocalDateTime recentlyMessageTime;

  public static RoomResponse from(
      final MessageOverview messageOverview,
      final Member interlocutor
  ) {
    return new RoomResponse(
        messageOverview.getRoomUUID(),
        interlocutor.getId(),
        interlocutor.getName(),
        interlocutor.getImageUrl(),
        messageOverview.getContent(),
        messageOverview.getCreatedAt()
    );
  }
}
