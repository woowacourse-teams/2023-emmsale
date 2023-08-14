package com.emmsale.report.domain;

import com.emmsale.base.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private Long reporterId;
  @Column(nullable = false)
  private Long reportedId;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ReportType type;
  @Column(nullable = false)
  private Long contentId;

  public Report(final Long reporterId, final Long reportedId, final ReportType type,
      final Long contentId) {
    this.reporterId = reporterId;
    this.reportedId = reportedId;
    this.type = type;
    this.contentId = contentId;
  }
}
