package com.emmsale.event.application.dto;

import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.member.domain.Member;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@RequiredArgsConstructor
@Getter
public class RecruitmentPostResponse {

  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  private final Long id;
  private final Long memberId;
  private final String name;
  private final String imageUrl;
  private final String content;
  @DateTimeFormat(pattern = DATE_TIME_FORMAT)
  private final LocalDate updatedAt;

  public static RecruitmentPostResponse from(final RecruitmentPost recruitmentPost) {
    final Member member = recruitmentPost.getMember();
    return new RecruitmentPostResponse(
        recruitmentPost.getId(),
        member.getId(),
        member.getName(),
        member.getImageUrl(),
        recruitmentPost.getContent(),
        recruitmentPost.getUpdatedAt().toLocalDate()
    );
  }
}
