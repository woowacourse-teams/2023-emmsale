package com.emmsale.member;

import com.emmsale.member.domain.Member;

public class MemberFixture {

  public static Member memberFixture() {
    final Member member = new Member(
        1234L,
        "https://avatars.githubusercontent.com/0/4",
        "아마란스"
    );
    member.updateName("우르");
    return member;
  }

  public static Member create(
      final Long githubId,
      final String githubUsername,
      final String name
  ) {
    final Member member = new Member(githubId, "imageUrl", githubUsername);
    member.updateName(name);
    return member;
  }
}
