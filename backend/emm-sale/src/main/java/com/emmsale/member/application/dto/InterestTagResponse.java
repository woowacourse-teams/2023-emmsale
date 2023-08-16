package com.emmsale.member.application.dto;

import com.emmsale.member.domain.InterestTag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InterestTagResponse {

  private final Long id;
  private final String name;

  public static List<InterestTagResponse> convertAllFrom(final List<InterestTag> interestTags) {
    return interestTags.stream()
        .map(InterestTagResponse::from)
        .collect(Collectors.toList());
  }

  public static InterestTagResponse from(final InterestTag interestTag) {
    return new InterestTagResponse(interestTag.getTag().getId(),
        interestTag.getTag().getName());
  }

}
