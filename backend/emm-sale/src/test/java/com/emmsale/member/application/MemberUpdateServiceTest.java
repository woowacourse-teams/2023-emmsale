package com.emmsale.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.MemberFixture;
import com.emmsale.member.application.dto.DescriptionRequest;
import com.emmsale.member.application.dto.OpenProfileUrlRequest;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

  @Nested
  @DisplayName("한줄 자기소개를 업데이트한다.")
  class UpdateDescription {

    @ParameterizedTest
    @ValueSource(strings = {"안녕하세요 김개발입니다.", "   <   짜잔   >  "})
    @DisplayName("정상적으로 업데이트된다.")
    void updateDescription_success(final String inputDescription) {
      // given
      final Member member = memberRepository.save(MemberFixture.memberFixture());

      final String expectDescription = inputDescription;
      final DescriptionRequest request = new DescriptionRequest(expectDescription);

      // when
      memberUpdateService.updateDescription(member, request);

      final Member actualMember = memberRepository.findById(member.getId()).get();

      // then
      assertThat(actualMember.getDescription()).isEqualTo(expectDescription);
    }

    @Test
    @DisplayName("한줄 자기소개가 100자를 초과하면 예외를 반환한다.")
    void updateDescription_fail() {
      // given
      final Member member = memberRepository.save(MemberFixture.memberFixture());

      final String invalidDescription = "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요!";
      final DescriptionRequest request = new DescriptionRequest(invalidDescription);

      // when
      final ThrowingCallable actual = () -> memberUpdateService.updateDescription(member, request);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(MemberException.class)
          .hasMessage(MemberExceptionType.OVER_LENGTH_DESCRIPTION.errorMessage());
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "        ", "\n", "\t"})
    @DisplayName("한줄 자기소개에 공백만 들어오면 빈 문자열로 업데이트한다.")
    void updateDescription_trim(final String inputDescription) {
      // given
      final Member member = memberRepository.save(MemberFixture.memberFixture());

      final String expectDescription = "";
      final DescriptionRequest request = new DescriptionRequest(inputDescription);

      // when
      memberUpdateService.updateDescription(member, request);

      final Member actualMember = memberRepository.findById(member.getId()).get();

      // then
      assertThat(actualMember.getDescription()).isEqualTo(expectDescription);
    }
  }
}
