package com.emmsale.member.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.MemberFixture;
import com.emmsale.member.application.dto.OpenProfileUrlRequest;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberUpdateServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MemberUpdateService memberUpdateService;
  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("오픈 프로필 URL을 업데이트한다.")
  void updateOpenProfileUrlTest() {
    // given
    final Member member = memberRepository.save(MemberFixture.memberFixture());

    final String expectOpenProfileUrl = "https://open.kakao.com/new/profile/url";
    final OpenProfileUrlRequest request = new OpenProfileUrlRequest(expectOpenProfileUrl);

    // when
    memberUpdateService.updateOpenProfileUrl(member, request);

    final Member actualMember = memberRepository.findById(member.getId()).get();

    // then
    assertThat(actualMember.getOpenProfileUrl()).isEqualTo(expectOpenProfileUrl);
  }
}
