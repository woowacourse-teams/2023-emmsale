package com.emmsale.report.application;

import static com.emmsale.event.EventFixture.eventFixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.RecruitmentPostRepository;
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.report.application.dto.ReportCreateRequest;
import com.emmsale.report.application.dto.ReportCreateResponse;
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
  private FeedRepository feedRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private RecruitmentPostRepository recruitmentPostRepository;

  @BeforeEach
  void init() {
    final Event event = eventRepository.save(eventFixture());
    final Member 신고자 = memberRepository.findById(1L).get();
    final Member 신고_대상자 = memberRepository.findById(2L).get();
    final Member feedWriter = memberRepository.save(new Member(111L, "img-url", "uname"));
    final Feed feed = feedRepository.save(new Feed(event, feedWriter, "피드 제목", "피드 내용"));
    신고자_ID = 신고자.getId();
    신고_대상자_ID = 신고_대상자.getId();
    commentRepository.save(Comment.createRoot(feed, 신고_대상자, "상대방에게 불쾌감을 줄 수 있는 내용"));
    commentRepository.save(Comment.createRoot(feed, 신고자, "그냥 댓글"));
    recruitmentPostRepository.save(new RecruitmentPost(신고_대상자, event, "사회적 논란을 불러일으킬 수 있는 내용"));
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
      final ReportCreateRequest request = new ReportCreateRequest(신고자_ID, wrongReportedId,
          ReportType.COMMENT,
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
      final ReportCreateRequest request = new ReportCreateRequest(신고자_ID, 신고자_ID,
          ReportType.COMMENT,
          abusingContentId);

      // when
      final ThrowingCallable actual = () -> reportCommandService.create(request, reporter);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(ReportException.class)
          .hasMessage(ReportExceptionType.FORBIDDEN_REPORT_MYSELF.errorMessage());
    }

    @Test
    @DisplayName("신고자가 자신이 아닐 경우 예외를 반환한다.")
    void create_fail_forbidden_report() {
      // given
      final Long otherMemberId = 2L;
      final Long reportedId = 1L;
      final Long abusingContentId = 1L;
      final Member reporter = memberRepository.findById(신고자_ID).get();
      final ReportCreateRequest request = new ReportCreateRequest(otherMemberId, reportedId,
          ReportType.COMMENT,
          abusingContentId);

      // when
      final ThrowingCallable actual = () -> reportCommandService.create(request, reporter);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(ReportException.class)
          .hasMessage(ReportExceptionType.REPORTER_MISMATCH.errorMessage());
    }

    @Test
    @DisplayName("이미 신고한 게시물을 한 번 더 신고할 경우 예외를 반환한다.")
    void create_fail_already_exist_report() {
      // given
      final Long abusingContentId = 1L;
      final Member reporter = memberRepository.findById(신고자_ID).get();
      final ReportCreateRequest request = new ReportCreateRequest(신고자_ID, 신고_대상자_ID,
          ReportType.COMMENT,
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
      final ReportCreateRequest request = new ReportCreateRequest(신고자_ID, 신고_대상자_ID,
          ReportType.COMMENT,
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
      final ReportCreateRequest request = new ReportCreateRequest(신고자_ID, 신고_대상자_ID,
          ReportType.COMMENT,
          abusingContentId);

      // when
      final ThrowingCallable actual = () -> reportCommandService.create(request, reporter);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(ReportException.class)
          .hasMessage(ReportExceptionType.REPORTED_MISMATCH_WRITER.errorMessage());
    }

    @Nested
    @DisplayName("신고자와 신고 대상자, 신고 유형 등을 입력하면 특정 게시물을 정상적으로 신고할 수 있다.")
    class CreateSuccess {

      @Test
      @DisplayName("댓글을 신고할 수 있다.")
      void create_success_comment() {
        // given
        final Long abusingContentId = 1L;
        final Member reporter = memberRepository.findById(신고자_ID).get();
        final ReportCreateRequest request = new ReportCreateRequest(신고자_ID, 신고_대상자_ID,
            ReportType.COMMENT,
            abusingContentId);
        final ReportCreateResponse expected = new ReportCreateResponse(1L, 신고자_ID, 신고_대상자_ID,
            ReportType.COMMENT, abusingContentId, null);

        // when
        final ReportCreateResponse actual = reportCommandService.create(request, reporter);

        // then
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("id", "createdAt")
            .isEqualTo(expected);

      }

      @Test
      @DisplayName("함께해요 게시글을 신고할 수 있다.")
      void create_success_participant() {
        // given
        final Long abusingContentId = 1L;
        final Member reporter = memberRepository.findById(신고자_ID).get();
        final ReportCreateRequest request = new ReportCreateRequest(신고자_ID, 신고_대상자_ID,
            ReportType.RECRUITMENT_POST,
            abusingContentId);
        final ReportCreateResponse expected = new ReportCreateResponse(1L, 신고자_ID, 신고_대상자_ID,
            ReportType.RECRUITMENT_POST, abusingContentId, null);

        // when
        final ReportCreateResponse actual = reportCommandService.create(request, reporter);

        // then
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("id", "createdAt")
            .isEqualTo(expected);
      }

      @Test
      @DisplayName("같이가요 요청을 신고할 수 있다.")
      void create_success_request_notification() {
        // given
        final Long abusingContentId = 1L;
        final Member reporter = memberRepository.findById(신고자_ID).get();
        final ReportCreateRequest request = new ReportCreateRequest(신고자_ID, 신고_대상자_ID,
            ReportType.REQUEST_NOTIFICATION,
            abusingContentId);
        final ReportCreateResponse expected = new ReportCreateResponse(1L, 신고자_ID, 신고_대상자_ID,
            ReportType.REQUEST_NOTIFICATION, abusingContentId, null);

        // when
        final ReportCreateResponse actual = reportCommandService.create(request, reporter);

        // then
        assertThat(actual)
            .usingRecursiveComparison()
            .ignoringFields("id", "createdAt")
            .isEqualTo(expected);
      }

      @Test
      @DisplayName("한 사용자의 여러 게시물을 연달아서 신고할 수 있다.")
      void create_success() {
        // given
        final Long abusingContentId = 1L;
        final Member reporter = memberRepository.findById(신고자_ID).get();
        final ReportCreateRequest commentRequest = new ReportCreateRequest(신고자_ID, 신고_대상자_ID,
            ReportType.COMMENT,
            abusingContentId);
        final ReportCreateResponse commentExpected = new ReportCreateResponse(1L, 신고자_ID, 신고_대상자_ID,
            ReportType.COMMENT, abusingContentId, null);
        final ReportCreateRequest recruitmentPostRequest = new ReportCreateRequest(신고자_ID,
            신고_대상자_ID,
            ReportType.RECRUITMENT_POST,
            abusingContentId);
        final ReportCreateResponse recruitmentPostExpected = new ReportCreateResponse(2L, 신고자_ID,
            신고_대상자_ID,
            ReportType.RECRUITMENT_POST, abusingContentId, null);
        final ReportCreateRequest requestNotificationRequest = new ReportCreateRequest(신고자_ID,
            신고_대상자_ID,
            ReportType.REQUEST_NOTIFICATION,
            abusingContentId);
        final ReportCreateResponse requestNotificationExpected = new ReportCreateResponse(3L,
            신고자_ID, 신고_대상자_ID,
            ReportType.REQUEST_NOTIFICATION, abusingContentId, null);

        // when
        final ReportCreateResponse commentActual = reportCommandService.create(commentRequest,
            reporter);
        final ReportCreateResponse recruitmentPostActual = reportCommandService.create(
            recruitmentPostRequest, reporter);
        final ReportCreateResponse requestNotificationActual = reportCommandService.create(
            requestNotificationRequest, reporter);

        // then
        Assertions.assertAll(
            () -> assertThat(commentActual)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt")
                .isEqualTo(commentExpected),
            () -> assertThat(recruitmentPostActual)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt")
                .isEqualTo(recruitmentPostExpected),
            () -> assertThat(requestNotificationActual)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt")
                .isEqualTo(requestNotificationExpected)
        );

      }

    }

  }

}
