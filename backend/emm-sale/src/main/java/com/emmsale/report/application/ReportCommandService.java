package com.emmsale.report.application;

import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.event.domain.repository.RecruitmentPostRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.domain.RequestNotification;
import com.emmsale.notification.domain.RequestNotificationRepository;
import com.emmsale.report.application.dto.ReportRequest;
import com.emmsale.report.application.dto.ReportResponse;
import com.emmsale.report.domain.Report;
import com.emmsale.report.domain.ReportType;
import com.emmsale.report.domain.repository.ReportRepository;
import com.emmsale.report.exception.ReportException;
import com.emmsale.report.exception.ReportExceptionType;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportCommandService {

  private final ReportRepository reportRepository;
  private final MemberRepository memberRepository;
  private final CommentRepository commentRepository;
  private final RecruitmentPostRepository recruitmentPostRepository;
  private final RequestNotificationRepository requestNotificationRepository;

  public ReportResponse create(final ReportRequest reportRequest, final Member member) {
    validateReportRequest(reportRequest, member);
    final Report report = reportRequest.toReport();

    return ReportResponse.of(reportRepository.save(report));
  }

  private void validateReportRequest(final ReportRequest reportRequest, final Member member) {
    validateReporterMismatch(reportRequest.getReporterId(), member);
    validateReportMySelf(reportRequest.getReportedId(), member);
    validateExistReportedMember(reportRequest.getReportedId());
    validateAlreadyExistReport(reportRequest);
    validateContent(reportRequest);
  }

  private void validateReporterMismatch(final Long reporterId, final Member member) {
    if (member.isNotMe(reporterId)) {
      throw new ReportException(ReportExceptionType.REPORTER_MISMATCH);
    }
  }

  private void validateReportMySelf(final Long reportedId, final Member member) {
    if (member.isMe(reportedId)) {
      throw new ReportException(ReportExceptionType.REPORT_MYSELF);
    }
  }

  private void validateExistReportedMember(final Long reportedId) {
    if (!memberRepository.existsById(reportedId)) {
      throw new ReportException(ReportExceptionType.NOT_FOUND_MEMBER);
    }
  }

  private void validateAlreadyExistReport(final ReportRequest reportRequest) {
    if (reportRepository.existsReportByReporterIdAndReportedId(
        reportRequest.getReporterId(), reportRequest.getReportedId())) {
      throw new ReportException(ReportExceptionType.ALREADY_EXIST_REPORT);
    }
  }

  private void validateContent(final ReportRequest reportRequest) {
    if (reportRequest.getType() == ReportType.COMMENT) {
      validateComment(reportRequest);
    }
    if (reportRequest.getType() == ReportType.RECRUITMENT_POST) {
      validateRecruitmentPost(reportRequest);
    }
    if (reportRequest.getType() == ReportType.REQUEST_NOTIFICATION) {
      validateRequestNotification(reportRequest);
    }
  }

  private void validateComment(final ReportRequest reportRequest) {
    Comment comment = commentRepository.findById(reportRequest.getContentId())
        .orElseThrow(() -> new ReportException(ReportExceptionType.NOT_FOUND_CONTENT));
    if (comment.getMember().getId().equals(reportRequest.getReportedId())) {
      throw new ReportException(ReportExceptionType.REPORTED_MISMATCH_WRITER);
    }
  }

  private void validateRecruitmentPost(final ReportRequest reportRequest) {
    RecruitmentPost recruitmentPost = recruitmentPostRepository.findById(
            reportRequest.getContentId())
        .orElseThrow(() -> new ReportException(ReportExceptionType.NOT_FOUND_CONTENT));
    if (recruitmentPost.getMember().getId().equals(reportRequest.getReportedId())) {
      throw new ReportException(ReportExceptionType.REPORTED_MISMATCH_WRITER);
    }
  }

  private void validateRequestNotification(final ReportRequest reportRequest) {
    RequestNotification requestNotification = requestNotificationRepository.findById(
            reportRequest.getContentId())
        .orElseThrow(() -> new ReportException(ReportExceptionType.NOT_FOUND_CONTENT));
    if (requestNotification.getSenderId().equals(reportRequest.getReportedId())) {
      throw new ReportException(ReportExceptionType.REPORTED_MISMATCH_WRITER);
    }
  }
}
