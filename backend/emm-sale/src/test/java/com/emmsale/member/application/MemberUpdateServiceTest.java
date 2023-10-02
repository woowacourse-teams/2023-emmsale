package com.emmsale.member.application;

import static com.emmsale.member.MemberFixture.memberFixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.application.dto.DescriptionRequest;
import com.emmsale.member.application.dto.OpenProfileUrlRequest;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

class MemberUpdateServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MemberUpdateService memberUpdateService;
  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("오픈 프로필 URL을 업데이트한다.")
  void updateOpenProfileUrlTest() {
    // given
    final Member member = memberRepository.save(memberFixture());

    final String expectOpenProfileUrl = "https://open.kakao.com/new/profile/url";
    final OpenProfileUrlRequest request = new OpenProfileUrlRequest(expectOpenProfileUrl);

    // when
    memberUpdateService.updateOpenProfileUrl(member, request);

    final Member actualMember = memberRepository.findById(member.getId()).get();

    // then
    assertThat(actualMember.getOptionalOpenProfileUrl().get()).isEqualTo(expectOpenProfileUrl);
  }

  @Nested
  @DisplayName("한줄 자기소개를 업데이트한다.")
  class UpdateDescription {

    @ParameterizedTest
    @ValueSource(strings = {"안녕하세요 김개발입니다.", "   <   짜잔   >  "})
    @DisplayName("정상적으로 업데이트된다.")
    void updateDescription_success(final String inputDescription) {
      // given
      final Member member = memberRepository.save(memberFixture());

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
      final Member member = memberRepository.save(memberFixture());

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
      final Member member = memberRepository.save(memberFixture());

      final String expectDescription = "";
      final DescriptionRequest request = new DescriptionRequest(inputDescription);

      // when
      memberUpdateService.updateDescription(member, request);

      final Member actualMember = memberRepository.findById(member.getId()).get();

      // then
      assertThat(actualMember.getDescription()).isEqualTo(expectDescription);
    }
  }

  @Nested
  @DisplayName("member와 memberId로 member를 삭제할 수 있다.")
  class DeleteMember {

    @Test
    @DisplayName("정상적으로 삭제하는 경우")
    void success() {
      //given
      final long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();

      //when
      memberUpdateService.deleteMember(member, memberId);

      //then
      assertThat(memberRepository.findById(memberId))
          .isEmpty();
    }

    @Test
    @DisplayName("member의 id와 memberId가 일치하지 않는 경우")
    void forbbidenDeleteMemberException() {
      final long memberId = 1L;
      final long otherMemberId = 2L;
      final Member member = memberRepository.findById(memberId).get();

      //when && then
      assertThatThrownBy(() -> memberUpdateService.deleteMember(member, otherMemberId))
          .isInstanceOf(MemberException.class)
          .hasMessage(MemberExceptionType.FORBIDDEN_DELETE_MEMBER.errorMessage());
    }
  }

  @Nested
  @DisplayName("member의 프로필 이미지가 업데이트 되는 경우, 기존 이미지 값을 지우고 새 이미지 값을 받는다.")
  class UpdateProfile {

    private final String imageName = "imageName";
    private final MultipartFile image = Mockito.mock(MultipartFile.class);

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @BeforeEach
    void setUp() {
      ReflectionTestUtils.setField(s3Client, "bucket", bucket);
      when(s3Client.uploadImage(image)).thenReturn(imageName);
      when(s3Client.convertImageUrl(anyString())).thenCallRealMethod();
      when(s3Client.convertImageName(anyString())).thenCallRealMethod();
    }

    @Test
    @DisplayName("기존 이미지는 깃허브 url인 경우 새로운 이미지를 s3 추가한다.")
    void pastGithubUrl() {
      //given
      final Member fixture = memberFixture();
      final Member member = memberRepository.save(fixture);

      //when
      memberUpdateService.updateMemberProfile(image, member.getId(), member);

      //then
      assertAll(
          () -> assertThat(member.getImageUrl())
              .isEqualTo(s3Client.convertImageUrl(imageName)),
          () -> verify(s3Client, times(1))
              .uploadImage(image)
      );
    }

    @Test
    @DisplayName("기존 이미지가 s3에 저장된 이미지인 경우, 기존 이미지를 지우고, 새로운 이미지를 추가한다.")
    void pastCustomImage() {
      //given
      final Member fixture = memberFixture();
      fixture.updateProfile(s3Client.convertImageUrl("already"));
      final Member member = memberRepository.save(fixture);

      //when
      memberUpdateService.updateMemberProfile(image, member.getId(), member);

      //then
      assertAll(
          () -> assertThat(member.getImageUrl())
              .isEqualTo(s3Client.convertImageUrl(imageName)),
          () -> verify(s3Client, times(1))
              .uploadImage(image),
          () -> verify(s3Client, times(1))
              .deleteImages(anyList())
      );
    }
  }
}
