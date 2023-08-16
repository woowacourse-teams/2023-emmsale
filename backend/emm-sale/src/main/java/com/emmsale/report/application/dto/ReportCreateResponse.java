package com.emmsale.report.application.dto;

import com.emmsale.report.domain.Report;
import com.emmsale.report.domain.ReportType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ReportCreateResponse {

  private final Long id;
  private final Long reporterId;
  private final Long reportedId;
  private final ReportType type;
  private final Long contentId;
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private final LocalDateTime createdAt;

  public static ReportCreateResponse from(final Report report) {
    return new ReportCreateResponse(report.getId(), report.getReporterId(),
        report.getReportedId(), report.getType(), report.getContentId(),
        report.getCreatedAt());
  }
}
