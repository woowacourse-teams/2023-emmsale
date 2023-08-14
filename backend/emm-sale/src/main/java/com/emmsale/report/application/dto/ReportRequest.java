package com.emmsale.report.application.dto;

import com.emmsale.report.domain.Report;
import com.emmsale.report.domain.ReportType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ReportRequest {

  private final Long reporterId;
  private final Long reportedId;
  private final ReportType type;
  private final Long contentId;

  public Report toReport() {
    return new Report(reporterId, reportedId, type, contentId);
  }
}
