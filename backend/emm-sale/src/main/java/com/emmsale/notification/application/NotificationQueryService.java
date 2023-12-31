package com.emmsale.notification.application;

import static com.emmsale.member.exception.MemberExceptionType.NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER;

import com.emmsale.member.domain.Member;
import com.emmsale.member.exception.MemberException;
import com.emmsale.notification.application.dto.NotificationDetailResponse;
import com.emmsale.notification.domain.NotificationRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService {

  private final NotificationRepository notificationRepository;

  public List<NotificationDetailResponse> findAllByMemberId(final Member authMember, final Long loginMemberId) {
    validateSameMember(authMember, loginMemberId);

    return notificationRepository.findAllByReceiverId(loginMemberId)
        .stream()
        .map(NotificationDetailResponse::from)
        .collect(Collectors.toList());
  }

  private void validateSameMember(final Member authMember, final Long loginMemberId) {
    if (authMember.isNotMe(loginMemberId)) {
      throw new MemberException(NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER);
    }
  }
}
