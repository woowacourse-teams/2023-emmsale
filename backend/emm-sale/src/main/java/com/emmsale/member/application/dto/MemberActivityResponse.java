package com.emmsale.member.application.dto;

import com.emmsale.member.domain.MemberActivity;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberActivityResponse {

  private final Long id;
  private final String name;
  private final String activityType;

  public static MemberActivityResponse from(final MemberActivity memberActivity) {
    return new MemberActivityResponse(
        memberActivity.getId(),
        memberActivity.getActivity().getName(),
        memberActivity.getActivity().getActivityType().getValue()
    );
  }
}
