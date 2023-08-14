package com.emmsale.report.application;

import static com.emmsale.event.EventFixture.eventFixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.block.domain.BlockRepository;
import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.RecruitmentPostRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.domain.RequestNotification;
import com.emmsale.notification.domain.RequestNotificationRepository;
import com.emmsale.report.application.dto.ReportRequest;
import com.emmsale.report.application.dto.ReportResponse;
import com.emmsale.report.domain.ReportType;
import com.emmsale.report.exception.ReportException;
import com.emmsale.report.exception.ReportExceptionType;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReportCommandServiceTest extends ServiceIntegrationTestHelper {

  private static Long 신고자_ID;
  private static Long 신고_대상자_ID;

  @Autowired
  private ReportCommandService reportCommandService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private BlockRepository blockRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private RecruitmentPostRepository recruitmentPostRepository;
  @Autowired
  private RequestNotificationRepository requestNotificationRepository;

  @BeforeEach
  void init() {
    final Event event = eventRepository.save(eventFixture());
    final Member 신고자 = memberRepository.findById(1L).get();
    final Member 신고_대상자 = memberRepository.findById(2L).get();
    신고자_ID = 신고자.getId();
    신고_대상자_ID = 신고_대상자.getId();
    commentRepository.save(Comment.createRoot(event, 신고_대상자, "상대방에게 불쾌감을 줄 수 있는 내용"));
    commentRepository.save(Comment.createRoot(event, 신고자, "그냥 댓글"));
    recruitmentPostRepository.save(new RecruitmentPost(신고_대상자, event, "사회적 논란을 불러일으킬 수 있는 내용"));
    requestNotificationRepository.save(
        new RequestNotification(신고_대상자_ID, 신고자_ID, event.getId(), "모욕감을 줄 수 있는 내용"));
  }

  @Nested
  @DisplayName("특정 게시글을 신고할 수 있다.")
  class Create {

    @Test
    @DisplayName("신고 대상자가 존재하지 않을 경우 예외를 반환한다.")
    void create_fail_not_found_member() {
      // given
      final Long wrongReportedId = 9999L;
      final Long abusingContentId = 1L;
      final Member reporter = memberRepository.findById(신고자_ID).get();
      final ReportRequest request = new ReportRequest(신고자_ID, wrongReportedId, ReportType.COMMENT,
          abusingContentId);

      // when
      final ThrowingCallable actual = () -> reportCommandService.create(request, reporter);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(ReportException.class)
          .hasMessage(ReportExceptionType.NOT_FOUND_MEMBER.errorMessage());
    }

    @Test
    @DisplayName("자신을 신고할 경우 예외를 반환한다.")
    void create_fail_report_self() {
      // given
      final Long abusingContentId = 1L;
      final Member reporter = memberRepository.findById(신고자_ID).get();
      final ReportRequest request = new ReportRequest(신고자_ID, 신고자_ID, ReportType.COMMENT,
          abusingContentId);

      // when
      final ThrowingCallable actual = () -> reportCommandService.create(request, reporter);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(ReportException.class)
          .hasMessage(ReportExceptionType.REPORT_MYSELF.errorMessage());
    }

    @Test
    @DisplayName("신고자가 자신이 아닐 경우 예외를 반환한다.")
    void create_fail_forbidden_report() {
      // given
      final Long otherMemberId = 2L;
      final Long reportedId = 1L;
      final Long abusingContentId = 1L;
      final Member reporter = memberRepository.findById(신고자_ID).get();
      final ReportRequest request = new ReportRequest(otherMemberId, reportedId, ReportType.COMMENT,
          abusingContentId);

      // when
      final ThrowingCallable actual = () -> reportCommandService.create(request, reporter);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(ReportException.class)
          .hasMessage(ReportExceptionType.REPORTER_MISMATCH.errorMessage());
    }

    @Test
    @DisplayName("이미 신고한 사용자를 한 번 더 신고할 경우 예외를 반환한다.")
    void create_fail_already_exist_report() {
      // given
      final Long abusingContentId = 1L;
      final Member reporter = memberRepository.findById(신고자_ID).get();
      final ReportRequest request = new ReportRequest(신고자_ID, 신고_대상자_ID, ReportType.COMMENT,
          abusingContentId);
      reportCommandService.create(request, reporter);

      // when
      final ThrowingCallable actual = () -> reportCommandService.create(request, reporter);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(ReportException.class)
          .hasMessage(ReportExceptionType.ALREADY_EXIST_REPORT.errorMessage());
    }

    @Test
    @DisplayName("존재하지 않는 게시물을 신고할 경우 예외를 반환한다.")
    void create_fail_not_found_content() {
      // given
      final Long abusingContentId = 9999L;
      final Member reporter = memberRepository.findById(신고자_ID).get();
      final ReportRequest request = new ReportRequest(신고자_ID, 신고_대상자_ID, ReportType.COMMENT,
          abusingContentId);

      // when
      final ThrowingCallable actual = () -> reportCommandService.create(request, reporter);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(ReportException.class)
          .hasMessage(ReportExceptionType.NOT_FOUND_CONTENT.errorMessage());
    }

    @Test
    @DisplayName("신고한 게시물이 신고 대상자의 게시물이 아닌 경우 예외를 반환한다.")
    void create_fail_reported_mismatch_writer() {
      // given
      final Long abusingContentId = 2L;
      final Member reporter = memberRepository.findById(신고자_ID).get();
      final ReportRequest request = new ReportRequest(신고자_ID, 신고_대상자_ID, ReportType.COMMENT,
          abusingContentId);

      // when
      final ThrowingCallable actual = () -> reportCommandService.create(request, reporter);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(ReportException.class)
          .hasMessage(ReportExceptionType.REPORTED_MISMATCH_WRITER.errorMessage());
    }

    @Nested
    @DisplayName("신고자와 신고 대상자, 신고 유형 등을 입력하면 특정 게시물을 정상적으로 신고할 수 있고, 게시물 작성자가 차단된다.")
    class CreateSuccess {

      @Test
      @DisplayName("댓글을 신고할 수 있다.")
      void create_success_comment() {
        // given
        final Long abusingContentId = 1L;
        final Member reporter = memberRepository.findById(신고자_ID).get();
        final ReportRequest request = new ReportRequest(신고자_ID, 신고_대상자_ID, ReportType.COMMENT,
            abusingContentId);
        final ReportResponse expected = new ReportResponse(1L, 신고자_ID, 신고_대상자_ID,
            ReportType.COMMENT, abusingContentId, null);

        // when
        final ReportResponse actual = reportCommandService.create(request, reporter);
        final boolean isBlocked = blockRepository.existsByRequestMemberIdAndBlockMemberId(신고자_ID,
            신고_대상자_ID);

        // then
        Assertions.assertAll(
            () -> assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt")
                .isEqualTo(expected),
            () -> assertThat(isBlocked).isTrue()
        );

      }

      @Test
      @DisplayName("함께해요 게시글을 신고할 수 있다.")
      void create_success_participant() {
        // given
        final Long abusingContentId = 1L;
        final Member reporter = memberRepository.findById(신고자_ID).get();
        final ReportRequest request = new ReportRequest(신고자_ID, 신고_대상자_ID,
            ReportType.RECRUITMENT_POST,
            abusingContentId);
        final ReportResponse expected = new ReportResponse(1L, 신고자_ID, 신고_대상자_ID,
            ReportType.RECRUITMENT_POST, abusingContentId, null);

        // when
        final ReportResponse actual = reportCommandService.create(request, reporter);
        final boolean isBlocked = blockRepository.existsByRequestMemberIdAndBlockMemberId(신고자_ID,
            신고_대상자_ID);

        // then
        Assertions.assertAll(
            () -> assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt")
                .isEqualTo(expected),
            () -> assertThat(isBlocked).isTrue()
        );
      }

      @Test
      @DisplayName("같이가요 요청을 신고할 수 있다.")
      void create_success_request_notification() {
        // given
        final Long abusingContentId = 1L;
        final Member reporter = memberRepository.findById(신고자_ID).get();
        final ReportRequest request = new ReportRequest(신고자_ID, 신고_대상자_ID,
            ReportType.REQUEST_NOTIFICATION,
            abusingContentId);
        final ReportResponse expected = new ReportResponse(1L, 신고자_ID, 신고_대상자_ID,
            ReportType.REQUEST_NOTIFICATION, abusingContentId, null);

        // when
        final ReportResponse actual = reportCommandService.create(request, reporter);
        final boolean isBlocked = blockRepository.existsByRequestMemberIdAndBlockMemberId(신고자_ID,
            신고_대상자_ID);

        // then
        Assertions.assertAll(
            () -> assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt")
                .isEqualTo(expected),
            () -> assertThat(isBlocked).isTrue()
        );
      }

    }

  }

}