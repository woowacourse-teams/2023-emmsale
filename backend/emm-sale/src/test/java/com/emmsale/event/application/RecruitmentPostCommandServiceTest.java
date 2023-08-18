package com.emmsale.event.application;

import static com.emmsale.event.EventFixture.createRecruitmentPostRequest;
import static com.emmsale.event.EventFixture.eventFixture;
import static com.emmsale.event.exception.EventExceptionType.ALREADY_CREATE_RECRUITMENT_POST;
import static com.emmsale.event.exception.EventExceptionType.FORBIDDEN_CREATE_RECRUITMENT_POST;
import static com.emmsale.event.exception.EventExceptionType.FORBIDDEN_UPDATE_RECRUITMENT_POST;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_RECRUITMENT_POST;
import static com.emmsale.event.exception.EventExceptionType.RECRUITMENT_POST_NOT_BELONG_EVENT;
import static com.emmsale.member.MemberFixture.memberFixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.emmsale.event.EventFixture;
import com.emmsale.event.application.dto.RecruitmentPostRequest;
import com.emmsale.event.application.dto.RecruitmentPostUpdateRequest;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.RecruitmentPostRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.MemberFixture;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RecruitmentPostCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private RecruitmentPostRepository recruitmentPostRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private RecruitmentPostCommandService postCommandService;

  @Nested
  @DisplayName("event에 함께해요 게시판에 글을 등록한다.")
  class CreateRecruitmentPost {

    @Test
    @DisplayName("정상적으로 멤버를 추가한다.")
    void success() {
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();
      final Event 인프콘 = eventRepository.save(eventFixture());

      final Long postId = postCommandService.createRecruitmentPost(인프콘.getId(),
          createRecruitmentPostRequest(member), member);

      assertThat(postId)
          .isNotNull();
    }

    @Test
    @DisplayName("memberId와 Member가 다르면 Exception이 발생한다.")
    void fail_forbidden() {
      final Long memberId = 1L;
      final Long otherMemberId = 2L;
      final Member member = memberRepository.findById(memberId).get();
      final RecruitmentPostRequest request = new RecruitmentPostRequest(otherMemberId, "빈 게시글");
      final Event 인프콘 = eventRepository.save(eventFixture());

      assertThatThrownBy(
          () -> postCommandService.createRecruitmentPost(인프콘.getId(), request, member))
          .isInstanceOf(EventException.class)
          .hasMessage(FORBIDDEN_CREATE_RECRUITMENT_POST.errorMessage());
    }

    @Test
    @DisplayName("이미 참가한 멤버면 Exception이 발생한다.")
    void fail_alreadyRecruit() {
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();
      final Event 인프콘 = eventRepository.save(eventFixture());
      final RecruitmentPostRequest request = createRecruitmentPostRequest(member);
      postCommandService.createRecruitmentPost(인프콘.getId(), request, member);

      assertThatThrownBy(
          () -> postCommandService.createRecruitmentPost(인프콘.getId(), request, member))
          .isInstanceOf(EventException.class)
          .hasMessage(ALREADY_CREATE_RECRUITMENT_POST.errorMessage());
    }
  }

  @Nested
  @DisplayName("이벤트 모집 게시글을 삭제할 수 있다.")
  class DeleteRecruitmentPost {

    @Test
    @DisplayName("정상적으로 모집글을 삭제한다.")
    void deleteRecruitmentPost_success() {
      //given
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();
      final Event event = eventRepository.save(eventFixture());

      final Long postId = postCommandService.createRecruitmentPost(event.getId(),
          createRecruitmentPostRequest(member), member);

      // when
      postCommandService.deleteRecruitmentPost(event.getId(), postId, member);
      final Optional<RecruitmentPost> actual = recruitmentPostRepository.findById(postId);

      // then
      assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 모집 게시글이면 예외가 발생한다.")
    void recruitmentPostNotFound() {
      // given
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();
      final long invalidPostId = 0L;

      final Long eventId = eventRepository.save(eventFixture()).getId();

      // when
      final ThrowingCallable actual = () -> postCommandService.deleteRecruitmentPost(eventId,
          invalidPostId,
          member);

      // then
      assertThatThrownBy(actual).isInstanceOf(EventException.class)
          .hasMessage(NOT_FOUND_RECRUITMENT_POST.errorMessage());
    }

    @Test
    @DisplayName("eventId가 유효하지 않으면 Exception을 발생한다.")
    void invalidEventId() {
      // given
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();
      final Event event = eventRepository.save(eventFixture());
      final Event otherEvent = eventRepository.save(EventFixture.모바일_컨퍼런스());

      final Long postId = postCommandService.createRecruitmentPost(event.getId(),
          createRecruitmentPostRequest(member), member);

      // when
      final ThrowingCallable actual = () -> postCommandService.deleteRecruitmentPost(
          otherEvent.getId(),
          postId,
          member
      );

      // then
      assertThatThrownBy(actual).isInstanceOf(EventException.class)
          .hasMessage(RECRUITMENT_POST_NOT_BELONG_EVENT.errorMessage());
    }

    @Test
    @DisplayName("참가 모집 게시글이")
    void invalidOwner() {
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();
      final Member otherMember = memberRepository.save(MemberFixture.memberFixture());
      final Event event = eventRepository.save(eventFixture());

      final Long postId = postCommandService.createRecruitmentPost(event.getId(),
          createRecruitmentPostRequest(member), member);

      // when
      final ThrowingCallable actual = () -> postCommandService.deleteRecruitmentPost(
          event.getId(),
          postId,
          otherMember
      );

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(EventException.class)
          .hasMessage(FORBIDDEN_UPDATE_RECRUITMENT_POST.errorMessage());
    }
  }

  @Nested
  @DisplayName("행사 참가 게시글을 수정할 수 있다.")
  class UpdateRecruitmentPost {

    private Member member;
    private Event event;
    private Long recruitmentPostId;

    @BeforeEach
    void setUp() {
      member = memberRepository.save(memberFixture());
      event = eventRepository.save(eventFixture());
      final RecruitmentPostRequest request = createRecruitmentPostRequest(member);
      recruitmentPostId = postCommandService.createRecruitmentPost(event.getId(), request, member);
    }

    @Test
    @DisplayName("정상적으로 성공하는 경우")
    void success() {
      //given
      final RecruitmentPostUpdateRequest request = new RecruitmentPostUpdateRequest("수정할 내용");

      //when
      postCommandService.updateRecruitmentPost(event.getId(), recruitmentPostId, request, member);

      //then
      final Optional<RecruitmentPost> updatedPost
          = recruitmentPostRepository.findById(recruitmentPostId);

      assertAll(
          () -> assertThat(updatedPost).isNotEmpty(),
          () -> assertThat(updatedPost.get().getContent())
              .isEqualTo(request.getContent())
      );
    }

    @Test
    @DisplayName("member가 행사참가 게시글의 소유자가 아닌 경우")
    void invalidOwner() {
      //given
      final RecruitmentPostUpdateRequest request = new RecruitmentPostUpdateRequest("변환할 내용");
      final Member otherMember = memberRepository.save(new Member(
          4321L,
          "이미지URL",
          "아마란스"
      ));

      //when && then
      assertThatThrownBy(
          () -> postCommandService.updateRecruitmentPost(event.getId(), recruitmentPostId, request,
              otherMember))
          .isInstanceOf(EventException.class)
          .hasMessage(FORBIDDEN_UPDATE_RECRUITMENT_POST.errorMessage());
    }

    @Test
    @DisplayName("eventId가 행사 참가 게시글의 event의 Id가 아닌 경우")
    void invalidEventId() {
      //given
      final RecruitmentPostUpdateRequest request = new RecruitmentPostUpdateRequest("변환할 내용");

      //when && then
      assertThatThrownBy(
          () -> postCommandService.updateRecruitmentPost(event.getId() + 1, recruitmentPostId,
              request,
              member))
          .isInstanceOf(EventException.class)
          .hasMessage(RECRUITMENT_POST_NOT_BELONG_EVENT.errorMessage());
    }
  }
}
