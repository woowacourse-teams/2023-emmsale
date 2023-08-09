package com.emmsale.report.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.report.application.dto.ReportRequest;
import com.emmsale.report.application.dto.ReportResponse;
import com.emmsale.report.domain.ReportReasonType;
import com.emmsale.report.domain.ReportType;
import com.emmsale.report.domain.repository.ReportRepository;
import com.emmsale.report.exception.ReportException;
import com.emmsale.report.exception.ReportExceptionType;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReportCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private ReportCommandService reportCommandService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private ReportRepository reportRepository;

  @Nested
  @DisplayName("특정 게시글을 신고할 수 있다.")
  class Create {

    @Test
    @DisplayName("신고자와 신고 대상자, 신고 유형 등을 입력하면 특정 게시물을 정상적으로 신고할 수 있고, 게시물 작성자가 차단된다.")
    void create_success() {
      // given
      final Long reporterId = 1L;
      final Long reportedId = 2L;
      final String abusingContent = "메롱메롱";
      final Member reporter = memberRepository.findById(reporterId).get();
      final ReportRequest request = new ReportRequest(reporterId, reportedId, abusingContent,
          ReportReasonType.ABUSE, ReportType.COMMENT);
      final ReportResponse expected = new ReportResponse(1L, reporterId, reportedId, abusingContent,
          ReportReasonType.ABUSE, ReportType.COMMENT, null);

      // when
      final ReportResponse actual = reportCommandService.create(request, reporter);
      // TODO: 2023-08-09 신고 대상자가 차단되었는지 여부를 검증하는 로직 추가
      // then
      assertThat(actual)
          .usingRecursiveComparison()
          .ignoringFields("id", "createdAt")
          .isEqualTo(expected);
    }

    @Test
    @DisplayName("신고 대상자가 존재하지 않을 경우 예외를 반환한다.")
    void create_fail_not_found_member() {
      // given
      final Long reporterId = 1L;
      final Long reportedId = 9999L;
      final String abusingContent = "메롱메롱";
      final Member reporter = memberRepository.findById(reporterId).get();
      final ReportRequest request = new ReportRequest(reporterId, reportedId, abusingContent,
          ReportReasonType.ABUSE, ReportType.COMMENT);

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
      final Long reporterId = 1L;
      final String abusingContent = "메롱메롱";
      final Member reporter = memberRepository.findById(reporterId).get();
      final ReportRequest request = new ReportRequest(reporterId, reporterId, abusingContent,
          ReportReasonType.ABUSE, ReportType.COMMENT);

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
      final Long reporterId = 1L;
      final Long otherMemberId = 2L;
      final Long reportedId = 1L;
      final String abusingContent = "메롱메롱";
      final Member reporter = memberRepository.findById(reporterId).get();
      final ReportRequest request = new ReportRequest(otherMemberId, reportedId, abusingContent,
          ReportReasonType.ABUSE, ReportType.COMMENT);

      // when
      final ThrowingCallable actual = () -> reportCommandService.create(request, reporter);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(ReportException.class)
          .hasMessage(ReportExceptionType.FORBIDDEN_REPORT.errorMessage());
    }

    @Test
    @DisplayName("이미 신고한 사용자를 한 번 더 신고할 경우 예외를 반환한다.")
    void create_fail_already_exist_report() {
      // given
      final Long reporterId = 1L;
      final Long reportedId = 2L;
      final String abusingContent = "메롱메롱";
      final Member reporter = memberRepository.findById(reporterId).get();
      final ReportRequest request = new ReportRequest(reporterId, reportedId, abusingContent,
          ReportReasonType.ABUSE, ReportType.COMMENT);
      reportCommandService.create(request, reporter);

      // when
      final ThrowingCallable actual = () -> reportCommandService.create(request, reporter);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(ReportException.class)
          .hasMessage(ReportExceptionType.ALREADY_EXIST_REPORT.errorMessage());
    }
  }

}