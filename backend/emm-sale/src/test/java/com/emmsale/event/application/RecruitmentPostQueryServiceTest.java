package com.emmsale.event.application;

import static com.emmsale.event.EventFixture.createRecruitmentPostRequest;
import static com.emmsale.event.EventFixture.eventFixture;
import static com.emmsale.event.EventFixture.인프콘_2023;
import static com.emmsale.member.MemberFixture.memberFixture;
import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.event.application.dto.RecruitmentPostRequest;
import com.emmsale.event.application.dto.RecruitmentPostResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.time.LocalDate;
import java.util.List;
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

  @Test
  @DisplayName("event의 id로 참가 게시글 목록을 조회할 수 있다.")
  void findRecruitmentPosts() {
    // given
    final Event 인프콘 = eventRepository.save(eventFixture());
    final Member 멤버1 = memberRepository.save(new Member(123L, "image1.com", "아마란스"));
    final Member 멤버2 = memberRepository.save(new Member(124L, "image2.com", "아마란스"));

    final RecruitmentPostRequest requestMember1 = createRecruitmentPostRequest(멤버1);
    final RecruitmentPostRequest requestMember2 = createRecruitmentPostRequest(멤버2);

    final Long 멤버1_참가글_ID = postCommandService
        .createRecruitmentPost(인프콘.getId(), requestMember1, 멤버1);
    final Long 멤버2_참가글_ID = postCommandService
        .createRecruitmentPost(인프콘.getId(), requestMember2, 멤버2);

    final List<RecruitmentPostResponse> expected = List.of(
        new RecruitmentPostResponse(멤버1_참가글_ID, 멤버1.getId(), 멤버1.getName(), 멤버1.getImageUrl(),
            requestMember1.getContent(), LocalDate.now()),
        new RecruitmentPostResponse(멤버2_참가글_ID, 멤버2.getId(), 멤버2.getName(), 멤버2.getImageUrl(),
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
    final Event 인프콘 = eventRepository.save(eventFixture());
    final Member 멤버1 = memberRepository.save(new Member(123L, "image1.com", "아마란스"));

    final RecruitmentPostRequest requestMember1 = createRecruitmentPostRequest(멤버1);

    final Long 멤버1_참가글_ID = postCommandService
        .createRecruitmentPost(인프콘.getId(), requestMember1, 멤버1);

    final RecruitmentPostResponse expected =
        new RecruitmentPostResponse(멤버1_참가글_ID, 멤버1.getId(), 멤버1.getName(), 멤버1.getImageUrl(),
            requestMember1.getContent(), LocalDate.now());

    //when
    final RecruitmentPostResponse actual = postQueryService.findRecruitmentPost(
        인프콘.getId(), 멤버1_참가글_ID);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Nested
  @DisplayName("이벤트에 이미 참가한 멤버인지 확인할 수 있다.")
  class isAlreadyRecruit {

    @Test
    @DisplayName("이벤트에 이미 참가한 경우 true를 반환한다.")
    void isExistThenTrue() {
      //given
      final Event 인프콘 = eventRepository.save(인프콘_2023());
      final Member 멤버 = memberRepository.save(memberFixture());
      postCommandService.createRecruitmentPost(인프콘.getId(), createRecruitmentPostRequest(멤버), 멤버);

      //when
      final Boolean actual = postQueryService.isAlreadyRecruit(인프콘.getId(), 멤버.getId());

      //then
      assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("이벤트에 참가히자 않은 경우 false를 반환한다.")
    void isNotExistThenFalse() {
      //given
      final Event 인프콘 = eventRepository.save(인프콘_2023());
      final Member 멤버 = memberRepository.save(memberFixture());

      //when
      final Boolean actual = postQueryService.isAlreadyRecruit(인프콘.getId(), 멤버.getId());

      //then
      assertThat(actual).isFalse();
    }
  }
}
