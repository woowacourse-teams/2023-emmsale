package com.emmsale.report.application;

import com.emmsale.member.domain.Member;
import com.emmsale.report.application.dto.ReportRequest;
import com.emmsale.report.application.dto.ReportResponse;
import com.emmsale.report.domain.Report;
import com.emmsale.report.domain.repository.ReportRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportCommandService {

  private final ReportRepository reportRepository;

  public ReportResponse create(final ReportRequest reportRequest, final Member member) {
    final Report report = new Report(reportRequest.getReporterId(), reportRequest.getReportedId(),
        reportRequest.getContent(), reportRequest.getReasonType(), reportRequest.getType());
    return ReportResponse.of(reportRepository.save(report));
  }

}
