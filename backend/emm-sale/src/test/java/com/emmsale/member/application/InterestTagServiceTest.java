package com.emmsale.member.application;

import static com.emmsale.tag.TagFixture.IOS;
import static com.emmsale.tag.TagFixture.백엔드;
import static com.emmsale.tag.TagFixture.안드로이드;
import static com.emmsale.tag.TagFixture.프론트엔드;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.application.dto.InterestTagAddRequest;
import com.emmsale.member.application.dto.InterestTagDeleteRequest;
import com.emmsale.member.application.dto.InterestTagResponse;
import com.emmsale.member.domain.InterestTag;
import com.emmsale.member.domain.InterestTagRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import com.emmsale.tag.exception.TagException;
import com.emmsale.tag.exception.TagExceptionType;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class InterestTagServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private InterestTagService interestTagService;
  @Autowired
  private InterestTagRepository interestTagRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private TagRepository tagRepository;

  private Member 사용자;
  private Tag 백엔드;
  private Tag 프론트엔드;
  private Tag 안드로이드;
  private Tag IOS;

  @BeforeEach
  void init() {
    사용자 = memberRepository.findById(1L).get();

    백엔드 = tagRepository.save(백엔드());
    프론트엔드 = tagRepository.save(프론트엔드());
    안드로이드 = tagRepository.save(안드로이드());
    IOS = tagRepository.save(IOS());

    interestTagRepository.save(new InterestTag(사용자, 백엔드));
    interestTagRepository.save(new InterestTag(사용자, 프론트엔드));
  }

  @Test
  @DisplayName("사용자 id를 입력으로 받으면 사용자의 관심 태그 리스트를 조회할 수 있다.")
  void findInterestTags() {
    // given
    final List<InterestTagResponse> expected = InterestTagResponse.convertAllFrom(
        List.of(new InterestTag(사용자, 백엔드), new InterestTag(사용자, 프론트엔드)));

    // when
    final List<InterestTagResponse> actual = interestTagService.findInterestTags(사용자.getId());

    // then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }


  @Nested
  @DisplayName("사용자의 관심 태그를 추가할 수 있다.")
  class AddInterestTag {

    @Test
    @DisplayName("Tag id 리스트를 입력으로 받으면 사용자의 관심 태그를 추가할 수 있다.")
    void addInterestTag_success() {
      // given
      final List<Long> tagIds = List.of(3L, 4L);
      final InterestTagAddRequest request = new InterestTagAddRequest(tagIds);

      final List<InterestTagResponse> expected = InterestTagResponse.convertAllFrom(
          List.of(new InterestTag(사용자, 백엔드), new InterestTag(사용자, 프론트엔드),
              new InterestTag(사용자, 안드로이드), new InterestTag(사용자, IOS)));

      // when
      final List<InterestTagResponse> actual = interestTagService.addInterestTag(사용자, request);

      // then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expected);
    }

    @Test
    @DisplayName("Tag id 리스트에 존재하지 않은 태그가 포함되어 있으면 예외를 반환한다.")
    void addInterestTag_fail_NOT_FOUND_TAG() {
      // given
      final List<Long> tagIds = List.of(3L, 4L, 99L);
      final InterestTagAddRequest request = new InterestTagAddRequest(tagIds);

      // when
      final ThrowingCallable actual = () -> interestTagService.addInterestTag(사용자, request);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(TagException.class)
          .hasMessage(TagExceptionType.NOT_FOUND_TAG.errorMessage());
    }

    @Test
    @DisplayName("이미 관심태그로 등록되어 있는 태그가 포함되어 있으면 예외를 반환한다.")
    void addInterestTag_fail_ALREADY_EXIST_INTEREST_TAG() {
      // given
      final List<Long> tagIds = List.of(3L, 4L, 1L);
      final InterestTagAddRequest request = new InterestTagAddRequest(tagIds);

      // when
      final ThrowingCallable actual = () -> interestTagService.addInterestTag(사용자, request);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(MemberException.class)
          .hasMessage(MemberExceptionType.ALREADY_EXIST_INTEREST_TAG.errorMessage());
    }

  }

  @Nested
  @DisplayName("사용자의 관심 태그를 삭제할 수 있다.")
  class deleteInterestTag {

    @Test
    @DisplayName("Tag id 리스트를 입력으로 받으면 사용자의 관심 태그를 삭제할 수 있다.")
    void deleteInterestTag() {
      // given
      final List<Long> tagIds = List.of(2L);
      final InterestTagDeleteRequest request = new InterestTagDeleteRequest(tagIds);

      final List<InterestTagResponse> expected = InterestTagResponse.convertAllFrom(
          List.of(new InterestTag(사용자, 백엔드)));

      // when
      final List<InterestTagResponse> actual = interestTagService.deleteInterestTag(사용자, request);

      // then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expected);
    }

    @Test
    @DisplayName("Tag id 리스트에 존재하지 않은 태그가 포함되어 있으면 예외를 반환한다.")
    void deleteInterestTag_fail_NOT_FOUND_TAG() {
      // given
      final List<Long> tagIds = List.of(1L, 99L);
      final InterestTagDeleteRequest request = new InterestTagDeleteRequest(tagIds);

      // when
      final ThrowingCallable actual = () -> interestTagService.deleteInterestTag(사용자, request);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(TagException.class)
          .hasMessage(TagExceptionType.NOT_FOUND_TAG.errorMessage());
    }

    @Test
    @DisplayName("관심 태그로 등록되지 않은 태그가 포함되어 있으면 예외를 반환한다.")
    void deleteInterestTag_fail_NOT_FOUND_INTEREST_TAG() {
      // given
      final List<Long> tagIds = List.of(1L, 4L);
      final InterestTagDeleteRequest request = new InterestTagDeleteRequest(tagIds);

      // when
      final ThrowingCallable actual = () -> interestTagService.deleteInterestTag(사용자, request);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(MemberException.class)
          .hasMessage(MemberExceptionType.NOT_FOUND_INTEREST_TAG.errorMessage());
    }
  }
}