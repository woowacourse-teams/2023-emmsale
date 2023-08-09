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
    if (member.isNotMe(reportRequest.getReporterId())) {
      throw new ReportException(ReportExceptionType.FORBIDDEN_REPORT);
    }
    if (member.isMe(reportRequest.getReportedId())) {
      throw new ReportException(ReportExceptionType.REPORT_MYSELF);
    }
    if (!memberRepository.existsById(reportRequest.getReportedId())) {
      throw new ReportException(ReportExceptionType.NOT_FOUND_MEMBER);
    }
    if (reportRepository.existsReportByReporterIdAndReportedId(reportRequest.getReporterId(),
        reportRequest.getReportedId())) {
      throw new ReportException(ReportExceptionType.ALREADY_EXIST_REPORT);
    }
    final Report report = reportRequest.toReport();

    return ReportResponse.of(reportRepository.save(report));
  }

}
