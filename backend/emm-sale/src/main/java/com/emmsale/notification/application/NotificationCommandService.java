package com.emmsale.notification.application;

import static com.emmsale.member.exception.MemberExceptionType.NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER;
import static com.emmsale.notification.exception.NotificationExceptionType.*;

import com.emmsale.member.domain.Member;
import com.emmsale.member.exception.MemberException;
import com.emmsale.notification.application.dto.NotificationDeleteRequest;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationRepository;
import com.emmsale.notification.exception.NotificationException;
import com.emmsale.notification.exception.NotificationExceptionType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class NotificationCommandService {

  private final NotificationRepository notificationRepository;

  public void read(final Member member, final Long notificationId) {
    final Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationException(NOT_FOUND_NOTIFICATION));

    validateSameMember(member, notification.getReceiverId());

    notification.read();
  }

  private void validateSameMember(final Member member, final Long loginMemberId) {
    if (member.isNotMe(loginMemberId)) {
      throw new MemberException(NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER);
    }
  }

  public void deleteBatch(
      final Member member,
      final NotificationDeleteRequest notificationDeleteRequest
  ) {
    final List<Long> deleteIds = notificationDeleteRequest.getDeleteIds();

    final List<Long> deleteIdsOwnMember = notificationRepository.findAllByIdIn(deleteIds)
        .stream()
        .filter(it -> it.isOwner(member.getId()))
        .map(Notification::getId)
        .collect(Collectors.toList());

    if (hasNoNotificationToDeleteBy(deleteIdsOwnMember)) {
      return;
    }

    notificationRepository.deleteBatchByIdsIn(deleteIdsOwnMember);
  }

  private boolean hasNoNotificationToDeleteBy(final List<Long> deleteIds) {
    return deleteIds.isEmpty();
  }
}
