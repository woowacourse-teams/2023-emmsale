package com.emmsale.report.application;

import com.emmsale.report.application.dto.ReportResponse;
import com.emmsale.report.domain.ReportReasonType;
import com.emmsale.report.domain.ReportType;
import com.emmsale.report.domain.repository.ReportRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportQueryService {

  private final ReportRepository reportRepository;

  public List<ReportResponse> findReports() {
    return List.of(
        new ReportResponse(1L, 1L, 2L, "메롱메롱", ReportReasonType.ABUSE, ReportType.COMMENT,
            LocalDateTime.parse("2023-08-09T13:25:00")),
        new ReportResponse(2L, 2L, 1L, "대충 심한 욕설", ReportReasonType.ABUSE, ReportType.PARTICIPANT,
            LocalDateTime.parse("2023-08-11T13:25:00")),
        new ReportResponse(3L, 1L, 3L, "사회적 물의를 일으킬 수 있는 발언", ReportReasonType.ABUSE,
            ReportType.REQUEST_NOTIFICATION,
            LocalDateTime.parse("2023-08-11T13:50:00")),
        new ReportResponse(4L, 4L, 1L, "도배글", ReportReasonType.ABUSE, ReportType.COMMENT,
            LocalDateTime.parse("2023-08-12T13:25:00"))

    );
  }
}
