package com.emmsale.report.application.dto;

import com.emmsale.report.domain.Report;
import com.emmsale.report.domain.ReportType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ReportResponse {

  private final Long id;
  private final Long reporterId;
  private final Long reportedId;
  private final ReportType type;
  private final Long contentId;
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private final LocalDateTime createdAt;

  public static ReportResponse of(Report report) {
    return new ReportResponse(report.getId(), report.getReporterId(),
        report.getReportedId(), report.getType(), report.getContentId(),
        report.getCreatedAt());
  }

  public static List<ReportResponse> of(List<Report> reports) {
    return reports.stream()
        .map(ReportResponse::of)
        .collect(Collectors.toList());
  }
}
