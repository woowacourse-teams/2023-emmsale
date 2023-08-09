package com.emmsale.report.application.dto;

import com.emmsale.report.domain.Report;
import com.emmsale.report.domain.ReportReasonType;
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
  private final String content;
  private final ReportReasonType reasonType;
  private final ReportType type;
  @JsonFormat(pattern = "yyyy:MM:dd:HH:mm:ss")
  private final LocalDateTime createdAt;

  public static ReportResponse of(Report report) {
    return new ReportResponse(report.getId(), report.getReporterId(),
        report.getReportedId(), report.getContent(), report.getReasonType(), report.getType(),
        report.getCreatedAt());
  }

  public static List<ReportResponse> of(List<Report> reports) {
    return reports.stream()
        .map(ReportResponse::of)
        .collect(Collectors.toList());
  }
}
