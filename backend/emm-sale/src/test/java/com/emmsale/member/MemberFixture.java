package com.emmsale.member;

import com.emmsale.member.domain.Member;

public class MemberFixture {

  public static Member memberFixture() {
    final Member member = new Member(
        1234L,
        "https://image-url.com",
        "아마란스"
    );
    member.updateName("우르");
    return member;
  }
}
