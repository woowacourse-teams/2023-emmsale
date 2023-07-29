package com.emmsale.event.application.dto;

import com.emmsale.event.domain.Participant;
import com.emmsale.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ParticipantResponse {

  private final Long id;
  private final Long memberId;
  private final String name;
  private final String imageUrl;
  private final String description;

  public static ParticipantResponse from(final Participant participant) {
    final Member member = participant.getMember();
    return new ParticipantResponse(participant.getId(), member.getId(), member.getName(),
        member.getImageUrl(), member.getDescription());
  }
}
