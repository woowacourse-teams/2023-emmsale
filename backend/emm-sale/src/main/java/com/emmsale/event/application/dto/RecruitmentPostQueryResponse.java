package com.emmsale.event.application.dto;

import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.member.domain.Member;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@RequiredArgsConstructor
@Getter
public class RecruitmentPostQueryResponse {

  private static final String DATE_TIME_FORMAT = "yyyy:MM:dd:HH:mm:ss";

  private final Long id;
  private final Long memberId;
  private final Long eventId;
  private final String content;
  @DateTimeFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDate updatedAt;

  public static RecruitmentPostQueryResponse from(final RecruitmentPost recruitmentPost) {
    final Member member = recruitmentPost.getMember();
    return new RecruitmentPostQueryResponse(
        recruitmentPost.getId(),
        member.getId(),
        recruitmentPost.getEvent().getId(),
        recruitmentPost.getContent(),
        recruitmentPost.getUpdatedAt().toLocalDate()
    );
  }
}
