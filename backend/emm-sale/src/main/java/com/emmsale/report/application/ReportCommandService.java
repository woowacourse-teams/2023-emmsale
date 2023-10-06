package com.emmsale.report.application;

import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.event.domain.repository.RecruitmentPostRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.report.application.dto.ReportCreateRequest;
import com.emmsale.report.application.dto.ReportCreateResponse;
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

  public ReportCreateResponse create(final ReportCreateRequest reportRequest, final Member member) {
    validateReportRequest(reportRequest, member);
    final Report report = reportRequest.toReport();
    return ReportCreateResponse.from(reportRepository.save(report));
  }


  private void validateReportRequest(final ReportCreateRequest reportRequest, final Member member) {
    validateReporterMismatch(reportRequest, member);
    validateReportMySelf(reportRequest, member);
    validateNotFoundReportedMember(reportRequest);
    validateAlreadyExistReport(reportRequest);
    validateContent(reportRequest);
  }

  private void validateReporterMismatch(final ReportCreateRequest reportRequest,
      final Member member) {
    if (member.isNotMe(reportRequest.getReporterId())) {
      throw new ReportException(ReportExceptionType.REPORTER_MISMATCH);
    }
  }

  private void validateReportMySelf(final ReportCreateRequest reportRequest, final Member member) {
    if (member.isMe(reportRequest.getReportedId())) {
      throw new ReportException(ReportExceptionType.FORBIDDEN_REPORT_MYSELF);
    }
  }

  private void validateNotFoundReportedMember(final ReportCreateRequest reportRequest) {
    if (!memberRepository.existsById(reportRequest.getReportedId())) {
      throw new ReportException(ReportExceptionType.NOT_FOUND_MEMBER);
    }
  }

  private void validateAlreadyExistReport(final ReportCreateRequest reportRequest) {
    if (reportRepository.existsReportByReporterIdAndContentIdAndType(
        reportRequest.getReporterId(), reportRequest.getContentId(), reportRequest.getType())) {
      throw new ReportException(ReportExceptionType.ALREADY_EXIST_REPORT);
    }
  }

  private void validateContent(final ReportCreateRequest reportRequest) {
    if (reportRequest.getType() == ReportType.COMMENT) {
      validateComment(reportRequest);
    }
    if (reportRequest.getType() == ReportType.RECRUITMENT_POST) {
      validateRecruitmentPost(reportRequest);
    }
  }

  private void validateComment(final ReportCreateRequest reportRequest) {
    Comment comment = commentRepository.findById(reportRequest.getContentId())
        .orElseThrow(() -> new ReportException(ReportExceptionType.NOT_FOUND_CONTENT));
    if (comment.isNotOwner(reportRequest.getReportedId())) {
      throw new ReportException(ReportExceptionType.REPORTED_MISMATCH_WRITER);
    }
  }

  private void validateRecruitmentPost(final ReportCreateRequest reportRequest) {
    RecruitmentPost recruitmentPost = recruitmentPostRepository.findById(
            reportRequest.getContentId())
        .orElseThrow(() -> new ReportException(ReportExceptionType.NOT_FOUND_CONTENT));
    if (recruitmentPost.isNotOwner(reportRequest.getReportedId())) {
      throw new ReportException(ReportExceptionType.REPORTED_MISMATCH_WRITER);
    }
  }
}
