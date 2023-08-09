package com.emmsale.event.application.dto;

import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RecruitmentPostResponse {

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

  public static RecruitmentPostResponse from(final RecruitmentPost recruitmentPost) {
    final Member member = recruitmentPost.getMember();
    return new RecruitmentPostResponse(
        recruitmentPost.getId(),
        member.getId(),
        member.getName(),
        member.getImageUrl(),
        member.getDescription(),
        recruitmentPost.getContent(),
        recruitmentPost.getCreatedAt().toLocalDate(),
        recruitmentPost.getUpdatedAt().toLocalDate()
    );
  }
}
