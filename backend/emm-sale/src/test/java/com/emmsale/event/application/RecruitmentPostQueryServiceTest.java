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
import com.emmsale.notification.application.RequestNotificationCommandService;
import com.emmsale.notification.application.dto.RequestNotificationModifyRequest;
import com.emmsale.notification.domain.RequestNotification;
import com.emmsale.notification.domain.RequestNotificationRepository;
import com.emmsale.notification.domain.RequestNotificationStatus;
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
  private RequestNotificationCommandService requestNotificationCommandService;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private RequestNotificationRepository requestNotificationRepository;

  @Test
  @DisplayName("event의 id로 참가 게시글 목록을 조회할 수 있다.")
  void findRecruitmentPosts() {
    // given
    final Event 인프콘 = eventRepository.save(eventFixture());
    final Member 게시글_작성자1 = memberRepository.save(new Member(123L, "image1.com"));
    final Member 게시글_작성자2 = memberRepository.save(new Member(124L, "image2.com"));
    final Member 게시글_작성자3 = memberRepository.save(new Member(125L, "image3.com"));
    final Member 게시글_작성자4 = memberRepository.save(new Member(126L, "image4.com"));
    final Member 게시글_조회자 = memberRepository.save(new Member(133L, "image.com"));

    final RecruitmentPostRequest requestMember1 = createRecruitmentPostRequest(게시글_작성자1);
    final RecruitmentPostRequest requestMember2 = createRecruitmentPostRequest(게시글_작성자2);
    final RecruitmentPostRequest requestMember3 = createRecruitmentPostRequest(게시글_작성자3);
    final RecruitmentPostRequest requestMember4 = createRecruitmentPostRequest(게시글_작성자4);

    final Long 게시글_작성자1_참가글_ID = postCommandService
        .createRecruitmentPost(인프콘.getId(), requestMember1, 게시글_작성자1);
    final Long 게시글_작성자2_참가글_ID = postCommandService
        .createRecruitmentPost(인프콘.getId(), requestMember2, 게시글_작성자2);
    final Long 게시글_작성자3_참가글_ID = postCommandService
        .createRecruitmentPost(인프콘.getId(), requestMember3, 게시글_작성자3);
    final Long 게시글_작성자4_참가글_ID = postCommandService
        .createRecruitmentPost(인프콘.getId(), requestMember4, 게시글_작성자4);

    final List<RecruitmentPostResponse> expected = List.of(
        new RecruitmentPostResponse(게시글_작성자1_참가글_ID, 게시글_작성자1.getId(), 게시글_작성자1.getName(),
            게시글_작성자1.getImageUrl(), requestMember1.getContent(), "NOT_REQUEST", LocalDate.now()),
        new RecruitmentPostResponse(게시글_작성자2_참가글_ID, 게시글_작성자2.getId(), 게시글_작성자2.getName(),
            게시글_작성자2.getImageUrl(), requestMember2.getContent(), "IN_PROGRESS", LocalDate.now()),
        new RecruitmentPostResponse(게시글_작성자3_참가글_ID, 게시글_작성자3.getId(), 게시글_작성자3.getName(),
            게시글_작성자3.getImageUrl(), requestMember3.getContent(), "ACCEPTED", LocalDate.now()),
        new RecruitmentPostResponse(게시글_작성자4_참가글_ID, 게시글_작성자4.getId(), 게시글_작성자4.getName(),
            게시글_작성자4.getImageUrl(), requestMember4.getContent(), "REJECTED", LocalDate.now())
    );

    //when
    requestNotificationRepository.save(
        new RequestNotification(게시글_조회자.getId(), 게시글_작성자2.getId(), 인프콘.getId(), "같이 가실래요?"));
    final Long acceptedRequestNotificationId = requestNotificationRepository.save(
        new RequestNotification(게시글_조회자.getId(), 게시글_작성자3.getId(), 인프콘.getId(), "같이 가실래요?")
    ).getId();
    final Long rejectedRequestNotificationId = requestNotificationRepository.save(
        new RequestNotification(게시글_조회자.getId(), 게시글_작성자4.getId(), 인프콘.getId(), "같이 가실래요?")
    ).getId();

    requestNotificationCommandService.modify(
        new RequestNotificationModifyRequest(RequestNotificationStatus.ACCEPTED),
        acceptedRequestNotificationId);
    requestNotificationCommandService.modify(
        new RequestNotificationModifyRequest(RequestNotificationStatus.REJECTED),
        rejectedRequestNotificationId);

    final List<RecruitmentPostResponse> actual = postQueryService.findRecruitmentPosts(인프콘.getId(),
        게시글_조회자);

    //then
    assertThat(actual)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("event의 id로 참가 게시글을 조회할 수 있다.")
  void findRecruitmentPost() {
    // given
    final Event 인프콘 = eventRepository.save(eventFixture());
    final Member 게시글_작성자 = memberRepository.save(new Member(123L, "image1.com"));
    final Member 게시글_조회자 = memberRepository.save(new Member(124L, "image2.com"));

    final RecruitmentPostRequest requestMember1 = createRecruitmentPostRequest(게시글_작성자);

    final Long 멤버1_참가글_ID = postCommandService
        .createRecruitmentPost(인프콘.getId(), requestMember1, 게시글_작성자);

    final RecruitmentPostResponse expected =
        new RecruitmentPostResponse(멤버1_참가글_ID, 게시글_작성자.getId(), 게시글_작성자.getName(),
            게시글_작성자.getImageUrl(),
            requestMember1.getContent(), "NOT_REQUEST", LocalDate.now());

    //when
    final RecruitmentPostResponse actual = postQueryService.findRecruitmentPost(
        인프콘.getId(), 멤버1_참가글_ID, 게시글_조회자);

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
