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

  public static List<InterestTagResponse> from(final List<InterestTag> interestTags) {
    return interestTags.stream()
        .map(interestTag -> new InterestTagResponse(interestTag.getTag().getId(),
            interestTag.getTag().getName()))
        .collect(Collectors.toList());
  }

}
