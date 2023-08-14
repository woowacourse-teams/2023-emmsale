package com.emmsale.notification.application;

import static com.emmsale.comment.exception.CommentExceptionType.NOT_FOUND_COMMENT;
import static com.emmsale.member.exception.MemberExceptionType.NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER;

import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.comment.exception.CommentException;
import com.emmsale.member.domain.Member;
import com.emmsale.member.exception.MemberException;
import com.emmsale.notification.application.dto.UpdateNotificationResponse;
import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.domain.UpdateNotificationRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UpdateNotificationQueryService {

  private final UpdateNotificationRepository updateNotificationRepository;
  private final CommentRepository commentRepository;

  public List<UpdateNotificationResponse> findAll(
      final Member authMember,
      final Long loginMemberId
  ) {
    validateSameMember(authMember, loginMemberId);

    final List<UpdateNotification> notifications =
        updateNotificationRepository.findAllByReceiverId(authMember.getId());

    return notifications.stream()
        .sorted(Comparator.comparing(UpdateNotification::getCreatedAt))
        .map(this::convertToResponse)
        .collect(Collectors.toList());
  }

  private void validateSameMember(final Member authMember, final Long loginMemberId) {
    if (authMember.isNotMe(loginMemberId)) {
      throw new MemberException(NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER);
    }
  }

  private UpdateNotificationResponse convertToResponse(final UpdateNotification notification) {
    if (notification.isCommentNotification()) {
      final Comment savedComment = commentRepository.findById(notification.getReceiverId())
          .orElseThrow(() -> new CommentException(NOT_FOUND_COMMENT));

      return UpdateNotificationResponse.convertCommentNotification(notification, savedComment);
    }

    return UpdateNotificationResponse.convertEventNotification(notification);
  }
}
