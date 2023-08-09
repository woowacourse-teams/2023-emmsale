package com.emmsale.report.application;

import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.report.application.dto.ReportRequest;
import com.emmsale.report.application.dto.ReportResponse;
import com.emmsale.report.domain.Report;
import com.emmsale.report.domain.repository.ReportRepository;
import com.emmsale.report.exception.ReportException;
import com.emmsale.report.exception.ReportExceptionType;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportCommandService {

  private final ReportRepository reportRepository;
  private final MemberRepository memberRepository;

  public ReportResponse create(final ReportRequest reportRequest, final Member member) {
    validateReportRequest(reportRequest, member);
    final Report report = reportRequest.toReport();

    return ReportResponse.of(reportRepository.save(report));
  }

  private void validateReportRequest(final ReportRequest reportRequest, final Member member) {
    validateReporterMismatch(reportRequest.getReporterId(), member);
    validateReportMySelf(reportRequest.getReportedId(), member);
    validateExistReportedMember(reportRequest.getReportedId());
    validateAlreadyExistReport(reportRequest);
  }

  private void validateReporterMismatch(final Long reporterId, final Member member) {
    if (member.isNotMe(reporterId)) {
      throw new ReportException(ReportExceptionType.REPORTER_MISMATCH);
    }
  }

  private void validateReportMySelf(final Long reportedId, final Member member) {
    if (member.isMe(reportedId)) {
      throw new ReportException(ReportExceptionType.REPORT_MYSELF);
    }
  }

  private void validateExistReportedMember(final Long reportedId) {
    if (!memberRepository.existsById(reportedId)) {
      throw new ReportException(ReportExceptionType.NOT_FOUND_MEMBER);
    }
  }

  private void validateAlreadyExistReport(final ReportRequest reportRequest) {
    if (reportRepository.existsReportByReporterIdAndReportedId(
        reportRequest.getReporterId(), reportRequest.getReportedId())) {
      throw new ReportException(ReportExceptionType.ALREADY_EXIST_REPORT);
    }
  }
}
