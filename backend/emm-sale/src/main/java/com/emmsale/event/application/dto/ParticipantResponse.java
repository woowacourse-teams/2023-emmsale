package com.emmsale.event.application.dto;

import com.emmsale.event.domain.Participant;
import com.emmsale.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
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
  private final String content;
  @JsonFormat(pattern = "yyyy.MM.dd")
  private final LocalDate createdAt;
  @JsonFormat(pattern = "yyyy.MM.dd")
  private final LocalDate updatedAt;

  public static ParticipantResponse from(final Participant participant) {
    final Member member = participant.getMember();
    return new ParticipantResponse(
        participant.getId(),
        member.getId(),
        member.getName(),
        member.getImageUrl(),
        member.getDescription(),
        participant.getContent(),
        participant.getCreatedAt().toLocalDate(),
        participant.getUpdatedAt().toLocalDate()
    );
  }
}
