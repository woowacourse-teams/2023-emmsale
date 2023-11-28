package com.emmsale.admin.report.api;

import com.emmsale.admin.report.application.ReportQueryService;
import com.emmsale.member.domain.Member;
import com.emmsale.report.application.dto.ReportFindResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportApi {

  private final ReportQueryService reportQueryService;

  @GetMapping
  public List<ReportFindResponse> findReports(final Member admin) {
    return reportQueryService.findReports(admin);
  }
}
