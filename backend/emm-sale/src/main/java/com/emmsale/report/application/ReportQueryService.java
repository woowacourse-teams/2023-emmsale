package com.emmsale.report.application;

import com.emmsale.report.application.dto.ReportResponse;
import com.emmsale.report.domain.repository.ReportRepository;
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
    return ReportResponse.of(reportRepository.findAll());
  }
}
