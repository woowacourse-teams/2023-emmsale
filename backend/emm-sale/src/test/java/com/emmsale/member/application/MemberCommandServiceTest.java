package com.emmsale.member.application;

import static com.emmsale.member.MemberFixture.memberFixture;
import static com.emmsale.member.exception.MemberExceptionType.FORBIDDEN_UPDATE_PROFILE_IMAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.application.dto.DescriptionRequest;
import com.emmsale.member.application.dto.MemberActivityInitialRequest;
import com.emmsale.member.domain.InterestTagRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import java.util.List;
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

class MemberCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MemberCommandService memberCommandService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private InterestTagRepository interestTagRepository;
  @Autowired
  private TagRepository tagRepository;

  @Test
  @DisplayName("Activity의 id를 통해서, 사용자의 Activity를 등록하고 사용자의 이름을 수정할 수 있다.")
  void registerActivities() throws Exception {
    //given
    tagRepository.save(new Tag("Backend"));
    final List<Long> activityIds = List.of(1L, 2L, 6L);
    final long savedMemberId = 1L;

    final Member member = memberRepository.findById(savedMemberId).get();
    final String updateName = "우르";

    final MemberActivityInitialRequest request = new MemberActivityInitialRequest(updateName,
        activityIds);

    //when & then
    assertAll(
        () -> assertDoesNotThrow(
            () -> memberCommandService.initializeMember(member, request)),
        () -> assertEquals(updateName, member.getName()),
        () -> assertEquals(1,
            interestTagRepository.findInterestTagsByMemberId(savedMemberId).size())
    );
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
      memberCommandService.updateDescription(member, request);

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
      final ThrowingCallable actual = () -> memberCommandService.updateDescription(member, request);

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
      memberCommandService.updateDescription(member, request);

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
      memberCommandService.deleteMember(member, memberId);

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
      assertThatThrownBy(() -> memberCommandService.deleteMember(member, otherMemberId))
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
      memberCommandService.updateMemberProfile(image, member.getId(), member);

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
      memberCommandService.updateMemberProfile(image, member.getId(), member);

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

    @Test
    @DisplayName("member와 memberId가 다른 경우 Exception을 throw한다.")
    void validateMemberIsNotMe() {
      //given
      final Member member = memberRepository.save(memberFixture());

      //when && then
      final ThrowingCallable testTarget = () ->
          memberCommandService.updateMemberProfile(image, member.getId() + 1, member);

      assertThatThrownBy(testTarget)
          .isInstanceOf(MemberException.class)
          .hasMessage(FORBIDDEN_UPDATE_PROFILE_IMAGE.errorMessage());
    }
  }
}
