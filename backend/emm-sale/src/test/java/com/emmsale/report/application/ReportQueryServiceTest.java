package com.emmsale.report.application;


import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.report.application.dto.ReportRequest;
import com.emmsale.report.application.dto.ReportResponse;
import com.emmsale.report.domain.ReportReasonType;
import com.emmsale.report.domain.ReportType;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReportQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private ReportQueryService reportQueryService;
  @Autowired
  private ReportCommandService reportCommandService;
  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("모든 신고 목록을 조회할 수 있다.")
  void findReports() {
    // given
    final Long reporterId = 1L;
    final Long reportedId = 2L;
    final String abusingContent = "메롱메롱";
    final Member reporter = memberRepository.findById(reporterId).get();
    final ReportRequest request = new ReportRequest(reporterId, reportedId, abusingContent,
        ReportReasonType.ABUSE, ReportType.COMMENT);
    final List<ReportResponse> expected = List.of(reportCommandService.create(request, reporter));

    // when
    List<ReportResponse> actual = reportQueryService.findReports();

    // then
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt")
        .isEqualTo(expected);

  }
}