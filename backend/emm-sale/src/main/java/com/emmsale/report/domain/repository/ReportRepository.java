package com.emmsale.report.domain.repository;

import com.emmsale.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

  boolean existsReportByReporterIdAndReportedId(final Long reporterId, final Long reportedId);

}
