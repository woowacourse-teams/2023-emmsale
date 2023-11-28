package com.emmsale.admin.report.application;

import static com.emmsale.admin.login.utils.AdminValidator.validateAuthorization;

import com.emmsale.member.domain.Member;
import com.emmsale.report.application.dto.ReportFindResponse;
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

  public List<ReportFindResponse> findReports(final Member admin) {
    validateAuthorization(admin);
    return ReportFindResponse.from(reportRepository.findAll());
  }
}
