package com.emmsale.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.login.application.dto.GithubProfileResponse;
import com.emmsale.login.application.dto.MemberQueryResponse;
import com.emmsale.member.application.dto.MemberActivityResponse;
import com.emmsale.member.application.dto.MemberDetailResponse;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
        .isEqualTo(actualResponse);
  }

  @Test
  @DisplayName("사용자를 조회하고 존재하지 않으므로 새로 생성하고 생성한 결과를 반환한다.")
  void findOrCreateMemberTestWithNewMember() {
    //given
    final MemberQueryResponse expectResponse = new MemberQueryResponse(4L, false);

    final GithubProfileResponse githubProfileFromGithub = new GithubProfileResponse("0", "name",
        "username", "https://imageUrl.com");

    //when
    final MemberQueryResponse actualResponse = memberQueryService.findOrCreateMember(
        githubProfileFromGithub);

    //then
    assertThat(expectResponse)
        .usingRecursiveComparison()
        .isEqualTo(actualResponse);
  }

  @Nested
  @DisplayName("사용자의 프로필 정보 조회")
  class FindProfile {

    @Test
    @DisplayName("사용자의 프로필 정보를 조회하면 조회 결과를 반환한다.")
    void findProfile_success() {
      //given
      final Long memberId = 1L;
      final MemberDetailResponse expect = new MemberDetailResponse(
          memberId,
          null,
          "",
          "https://imageurl.com",
          "https://github.com/amaran-th",
          List.of(
              new MemberActivityResponse(1L, "YAPP", "동아리"),
              new MemberActivityResponse(2L, "DND", "동아리"),
              new MemberActivityResponse(3L, "nexters", "동아리")
          )
      );

      //when
      final MemberDetailResponse actual = memberQueryService.findProfile(memberId);

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .ignoringFields("description")
          .isEqualTo(expect);
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 프로필 정보를 조회하면 예외를 반환한다.")
    void findProfile_fail() {
      //given
      final Long notFoundMemberId = 0L;

      //when
      final ThrowingCallable actual = () -> memberQueryService.findProfile(notFoundMemberId);

      //then
      assertThatThrownBy(actual)
          .isInstanceOf(MemberException.class)
          .hasMessage(MemberExceptionType.NOT_FOUND_MEMBER.errorMessage());
    }
  }
}
