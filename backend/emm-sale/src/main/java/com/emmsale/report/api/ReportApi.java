package com.emmsale.report.api;

import com.emmsale.member.domain.Member;
import com.emmsale.report.application.ReportCommandService;
import com.emmsale.report.application.ReportQueryService;
import com.emmsale.report.application.dto.ReportCreateRequest;
import com.emmsale.report.application.dto.ReportCreateResponse;
import com.emmsale.report.application.dto.ReportFindResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportApi {

  private final ReportCommandService reportCommandService;
  private final ReportQueryService reportQueryService;

  @PostMapping("/reports")
  @ResponseStatus(HttpStatus.CREATED)
  public ReportCreateResponse create(@RequestBody final ReportCreateRequest reportRequest,
      final Member member) {
    return reportCommandService.create(reportRequest, member);
  }

  @GetMapping("/reports")
  public List<ReportFindResponse> findReports() {
    return reportQueryService.findReports();
  }
}
