package com.emmsale.report.application.dto;

import com.emmsale.report.domain.Report;
import com.emmsale.report.domain.ReportReasonType;
import com.emmsale.report.domain.ReportType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ReportRequest {

  private final Long reporterId;
  private final Long reportedId;
  private final String content;
  private final ReportReasonType reasonType;
  private final ReportType type;

  public Report toReport() {
    return new Report(reporterId, reportedId, content, reasonType, type);
  }
}
