package com.emmsale.member;

import com.emmsale.member.domain.Member;

public class MemberFixture {

  public static Member memberFixture() {
    final Member member = new Member(1234L,
        "https://avatars.githubusercontent.com/0/4",
        "아마란스"
    );
    member.updateName("우르");
    return member;
  }

  public static Member generalMember() {
    final Member member = new Member(1L,
        1234L,
        "https://avatars.githubusercontent.com/0/4",
        "아마란스",
        "amaran-th"
    );
    member.updateName("우르");
    return member;
  }

  public static Member adminMember() {
    final long adminMemberId = 3L;
    final Member member = new Member(adminMemberId,
        1234L,
        "https://avatars.githubusercontent.com/0/4",
        "아마란스",
        "amaran-th"
    );
    member.updateName("관리자");
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
