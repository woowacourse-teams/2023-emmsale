package com.emmsale.message_room.application.dto;

import com.emmsale.member.application.dto.MemberReferenceResponse;
import com.emmsale.member.domain.Member;
import com.emmsale.message_room.domain.Message;
import com.emmsale.message_room.infrastructure.persistence.dto.MessageOverview;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RoomResponse {

  private final String roomId;
  private final MemberReferenceResponse interlocutor;
  private final MessageResponse recentlyMessage;

  public static RoomResponse from(
      final MessageOverview messageOverview,
      final Member interlocutor
  ) {
    return new RoomResponse(
        messageOverview.getRoomUUID(),
        MemberReferenceResponse.from(interlocutor),
        MessageResponse.from(new Message(
            messageOverview.getId(),
            messageOverview.getContent(),
            messageOverview.getSender(),
            messageOverview.getRoomUUID(),
            messageOverview.getCreatedAt()))
    );
  }
}
