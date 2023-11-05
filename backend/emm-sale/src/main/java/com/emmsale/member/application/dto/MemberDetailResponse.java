package com.emmsale.member.application.dto;

import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberActivity;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class MemberDetailResponse {

  private static final String GITHUB_URL_PREFIX = "https://github.com/";

  private final Long id;
  private final String name;
  private final String description;
  private final String imageUrl;
  private final String githubUrl;
  private final List<MemberActivityResponse> activities;

  public static MemberDetailResponse of(
      final Member member,
      final List<MemberActivity> activities
  ) {
    final List<MemberActivityResponse> memberActivityResponses = activities.stream()
        .map(MemberActivityResponse::from)
        .collect(toUnmodifiableList());

    return new MemberDetailResponse(
        member.getId(),
        member.getName(),
        member.getDescription(),
        member.getImageUrl(),
        GITHUB_URL_PREFIX + member.getGithubUsername(),
        memberActivityResponses
    );
  }
}
