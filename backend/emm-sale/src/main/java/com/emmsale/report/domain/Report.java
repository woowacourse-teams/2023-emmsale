package com.emmsale.report.domain;

import com.emmsale.base.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
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
  private String content;
  @Column(nullable = false)
  private ReportType type;

  public Report(final Long reporterId, final Long reportedId, final String content,
      final ReportType type) {
    this.reporterId = reporterId;
    this.reportedId = reportedId;
    this.content = content;
    this.type = type;
  }
}
