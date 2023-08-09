package com.emmsale.report.application.dto;

import com.emmsale.report.domain.ReportReasonType;
import com.emmsale.report.domain.ReportType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
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
}
