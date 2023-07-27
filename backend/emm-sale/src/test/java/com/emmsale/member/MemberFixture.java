package com.emmsale.member;

import com.emmsale.member.domain.Member;

public class MemberFixture {

  public static Member memberFixture() {
    return new Member(
        1234L,
        "https://image-url.com",
        "member"
    );
  }
}
