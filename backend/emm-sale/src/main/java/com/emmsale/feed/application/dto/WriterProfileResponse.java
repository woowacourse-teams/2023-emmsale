package com.emmsale.feed.application.dto;

import com.emmsale.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WriterProfileResponse {

  private final Long memberId;
  private final String name;
  private final String imageUrl;

  public static WriterProfileResponse from(final Member writer) {
    return new WriterProfileResponse(
        writer.getId(),
        writer.getName(),
        writer.getImageUrl()
    );
  }
}
