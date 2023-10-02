package com.emmsale.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.emmsale.member.MemberFixture;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MemberTest {

  @Nested
  @DisplayName("한줄 자기소개를 업데이트한다.")
  class UpdateDescription {

    @ParameterizedTest
    @ValueSource(strings = {"안녕하세요 김개발입니다.", "   <   짜잔   >  "})
    @DisplayName("정상적으로 업데이트 된다.")
    void updateDescription_success(final String inputDescription) {
      // given
      Member member = MemberFixture.memberFixture();

      //when
      member.updateDescription(inputDescription);
      String actual = member.getDescription();

      // then
      assertThat(actual).isEqualTo(inputDescription);
    }

    @Test
    @DisplayName("한줄 자기소개가 100자를 초과하면 예외를 반환한다.")
    void updateDescription_fail() {
      // given
      Member member = MemberFixture.memberFixture();

      final String invalidDescription = "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요!";

      // when
      final ThrowingCallable actual = () -> member.updateDescription(invalidDescription);

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
      final Member member = MemberFixture.memberFixture();

      //when
      member.updateDescription(inputDescription);
      final String actual = member.getDescription();

      // then
      assertThat(actual).isEmpty();
    }
  }

  @Nested
  @DisplayName("멤버가 자신의 프로필이미지가 github인지 판단한다.")
  class IsNotGithubProfile {

    @Test
    void falseCase() {
      final Member member = MemberFixture.memberFixture();
      member.updateProfile("https://avatars.githubusercontent.com/o/v4");

      assertFalse(member.isNotGithubProfile());
    }

    @Test
    void trueCase() {
      final Member member = MemberFixture.memberFixture();

      assertTrue(member.isNotGithubProfile());
    }
  }

}
