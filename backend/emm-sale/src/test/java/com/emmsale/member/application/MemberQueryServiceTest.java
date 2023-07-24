package com.emmsale.member.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.login.application.dto.GithubProfileResponse;
import com.emmsale.login.application.dto.MemberQueryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MemberQueryService memberQueryService;

  @Test
  @DisplayName("사용자를 조회하고 조회 결과를 반환한다.")
  void findOrCreateMemberTest() {
    //given
    final MemberQueryResponse expectResponse = new MemberQueryResponse(1L, false);

    final GithubProfileResponse githubProfileFromGithub = new GithubProfileResponse("1", "name",
        "username", "https://imageUrl.com");

    //when
    final MemberQueryResponse actualResponse = memberQueryService.findOrCreateMember(
        githubProfileFromGithub);

    //then
    assertThat(expectResponse)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(actualResponse);
  }

  @Test
  @DisplayName("사용자를 조회하고 존재하지 않으므로 새로 생성하고 생성한 결과를 반환한다.")
  void findOrCreateMemberTestWithNewMember() {
    //given
    final MemberQueryResponse expectResponse = new MemberQueryResponse(3L, true);

    final GithubProfileResponse githubProfileFromGithub = new GithubProfileResponse("0", "name",
        "username", "https://imageUrl.com");

    //when
    final MemberQueryResponse actualResponse = memberQueryService.findOrCreateMember(
        githubProfileFromGithub);

    //then
    assertThat(expectResponse)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(actualResponse);
  }
}
