package com.emmsale.report.application;

import com.emmsale.report.application.dto.ReportRequest;
import com.emmsale.report.application.dto.ReportResponse;
import com.emmsale.report.domain.ReportReasonType;
import com.emmsale.report.domain.ReportType;
import com.emmsale.report.domain.repository.ReportRepository;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportCommandService {

  private final ReportRepository reportRepository;

  public ReportResponse create(final ReportRequest reportRequest) {
    return new ReportResponse(1L, 1L, 2L, "메롱메롱", ReportReasonType.ABUSE, ReportType.COMMENT,
        LocalDateTime.parse("2023-08-09T13:25:00"));
  }

}
