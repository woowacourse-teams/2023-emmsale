package com.emmsale.event.application;

import static com.emmsale.event.EventFixture.createRecruitmentPostRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import com.emmsale.event.EventFixture;
import com.emmsale.event.application.dto.RecruitmentPostQueryResponse;
import com.emmsale.event.application.dto.RecruitmentPostRequest;
import com.emmsale.event.application.dto.RecruitmentPostResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.RecruitmentPostRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RecruitmentPostQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private RecruitmentPostQueryService postQueryService;
  @Autowired
  private RecruitmentPostCommandService postCommandService;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private RecruitmentPostRepository recruitmentPostRepository;

  private Member 사용자1;
  private Member 사용자2;
  private Event 인프콘;
  private Event 구름톤;

  @BeforeEach
  void setUp() {
    사용자1 = memberRepository.findById(1L).get();
    사용자2 = memberRepository.findById(2L).get();
    인프콘 = eventRepository.save(EventFixture.인프콘_2023());
    구름톤 = eventRepository.save(EventFixture.구름톤());
  }

  @Test
  @DisplayName("event의 id로 참가 게시글 목록을 조회할 수 있다.")
  void findRecruitmentPosts() {
    // given
    final RecruitmentPostRequest requestMember1 = createRecruitmentPostRequest(사용자1);
    final RecruitmentPostRequest requestMember2 = createRecruitmentPostRequest(사용자2);

    final Long 멤버1_참가글_ID = postCommandService
        .createRecruitmentPost(인프콘.getId(), requestMember1, 사용자1);
    final Long 멤버2_참가글_ID = postCommandService
        .createRecruitmentPost(인프콘.getId(), requestMember2, 사용자2);

    final List<RecruitmentPostResponse> expected = List.of(
        new RecruitmentPostResponse(멤버1_참가글_ID, 사용자1.getId(), 사용자1.getName(), 사용자1.getImageUrl(),
            requestMember1.getContent(), LocalDate.now()),
        new RecruitmentPostResponse(멤버2_참가글_ID, 사용자2.getId(), 사용자2.getName(), 사용자2.getImageUrl(),
            requestMember2.getContent(), LocalDate.now())
    );

    //when
    final List<RecruitmentPostResponse> actual = postQueryService.findRecruitmentPosts(인프콘.getId());

    //then
    assertThat(actual)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("event의 id로 참가 게시글 목록을 조회할 수 있다.")
  void findRecruitmentPost() {
    // given
    final RecruitmentPostRequest requestMember1 = createRecruitmentPostRequest(사용자1);

    final Long 멤버1_참가글_ID = postCommandService
        .createRecruitmentPost(인프콘.getId(), requestMember1, 사용자1);

    final RecruitmentPostResponse expected =
        new RecruitmentPostResponse(멤버1_참가글_ID, 사용자1.getId(), 사용자1.getName(), 사용자1.getImageUrl(),
            requestMember1.getContent(), LocalDate.now());

    //when
    final RecruitmentPostResponse actual = postQueryService.findRecruitmentPost(
        인프콘.getId(), 멤버1_참가글_ID);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("사용자의 모든 함께가기 요청 목록을 조회한다.")
  void findRecruitmentPostsByMemberIdTest() {
    //given
    final RecruitmentPost savedPost1 = recruitmentPostRepository.save(
        new RecruitmentPost(사용자1, 인프콘, "함께해요~"));
    final RecruitmentPost savedPost2 = recruitmentPostRepository.save(
        new RecruitmentPost(사용자1, 구름톤, "같이 가요~"));

    final List<RecruitmentPostQueryResponse> expectResponse = List.of(savedPost1, savedPost2)
        .stream()
        .map(RecruitmentPostQueryResponse::from)
        .collect(Collectors.toList());

    //when
    final List<RecruitmentPostQueryResponse> actualResponse = postQueryService.findRecruitmentPostsByMemberId(
        사용자1, 사용자1.getId());

    //then
    assertThat(actualResponse)
        .usingRecursiveComparison()
        .isEqualTo(expectResponse);
  }

  @Test
  @DisplayName("다른 사용자의 함께가기 요청 목록을 조회할 경우 NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER 타입의 MemberException이 발생한다.")
  void findRecruitmentPostsByMemberIdWithAnotherMemberIdTest() {
    //given
    final MemberExceptionType expectExceptionType = MemberExceptionType.NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER;

    //when
    final MemberException actualException = assertThrowsExactly(MemberException.class,
        () -> postQueryService.findRecruitmentPostsByMemberId(사용자1, 사용자2.getId())
    );

    //then
    assertEquals(expectExceptionType, actualException.exceptionType());
  }

  @Nested
  @DisplayName("이벤트에 이미 참가한 멤버인지 확인할 수 있다.")
  class isAlreadyRecruit {

    @Test
    @DisplayName("이벤트에 이미 참가한 경우 true를 반환한다.")
    void isExistThenTrue() {
      //given
      postCommandService.createRecruitmentPost(인프콘.getId(), createRecruitmentPostRequest(사용자1),
          사용자1);

      //when
      final Boolean actual = postQueryService.isAlreadyRecruit(인프콘.getId(), 사용자1.getId());

      //then
      assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("이벤트에 참가히자 않은 경우 false를 반환한다.")
    void isNotExistThenFalse() {
      //when
      final Boolean actual = postQueryService.isAlreadyRecruit(인프콘.getId(), 사용자1.getId());

      //then
      assertThat(actual).isFalse();
    }
  }
}
