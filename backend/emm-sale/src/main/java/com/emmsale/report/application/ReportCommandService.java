package com.emmsale.report.application;

import com.emmsale.block.domain.Block;
import com.emmsale.block.domain.BlockRepository;
import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.event.domain.repository.RecruitmentPostRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.domain.RequestNotification;
import com.emmsale.notification.domain.RequestNotificationRepository;
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
  private final RequestNotificationRepository requestNotificationRepository;
  private final BlockRepository blockRepository;

  public ReportCreateResponse create(final ReportCreateRequest reportRequest, final Member member) {
    validateReportRequest(reportRequest, member);
    final Report report = reportRequest.toReport();
    blockReportedMember(reportRequest);
    return ReportCreateResponse.from(reportRepository.save(report));
  }


  private void validateReportRequest(final ReportCreateRequest reportRequest, final Member member) {
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

  private void validateAlreadyExistReport(final ReportCreateRequest reportRequest) {
    if (reportRepository.existsReportByReporterIdAndReportedId(
        reportRequest.getReporterId(), reportRequest.getReportedId())) {
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
    if (reportRequest.getType() == ReportType.REQUEST_NOTIFICATION) {
      validateRequestNotification(reportRequest);
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

  private void validateRequestNotification(final ReportCreateRequest reportCreateRequest) {
    RequestNotification requestNotification = requestNotificationRepository.findById(
            reportCreateRequest.getContentId())
        .orElseThrow(() -> new ReportException(ReportExceptionType.NOT_FOUND_CONTENT));
    if (requestNotification.isNotSender(reportCreateRequest.getReportedId())) {
      throw new ReportException(ReportExceptionType.REPORTED_MISMATCH_WRITER);
    }
  }

  private void blockReportedMember(final ReportCreateRequest reportCreateRequest) {
    final Long reporterId = reportCreateRequest.getReporterId();
    final Long reportedId = reportCreateRequest.getReportedId();
    if (!blockRepository.existsByRequestMemberIdAndBlockMemberId(reporterId, reportedId)) {
      final Block block = new Block(reporterId, reportedId);
      blockRepository.save(block);
    }
  }
}
