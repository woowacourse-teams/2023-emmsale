package com.emmsale.report.domain.repository;

import com.emmsale.report.domain.Report;
import com.emmsale.report.domain.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

  boolean existsReportByReporterIdAndContentIdAndType(final Long reporterId, final Long contentId,
      final ReportType type);

}
