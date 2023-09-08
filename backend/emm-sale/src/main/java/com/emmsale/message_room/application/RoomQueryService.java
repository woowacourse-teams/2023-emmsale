package com.emmsale.message_room.application;

import static com.emmsale.member.exception.MemberExceptionType.NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER;

import com.emmsale.member.domain.Member;
import com.emmsale.member.exception.MemberException;
import com.emmsale.message_room.application.dto.RoomResponse;
import com.emmsale.message_room.infrastructure.persistence.MessageDao;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomQueryService {

  private final MessageDao messageDao;

  public List<RoomResponse> findAll(final Member loginMember, final Long memberId) {
    validateSameMember(loginMember, memberId);

    return messageDao.findRecentlyMessages(memberId)
        .stream()
        .map(RoomResponse::from)
        .sorted(Comparator.comparing(RoomResponse::getRecentlyMessageTime).reversed())
        .collect(Collectors.toList());
  }

  private void validateSameMember(final Member loginMember, final Long memberId) {
    if (loginMember.isNotMe(memberId)) {
      throw new MemberException(NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER);
    }
  }
}
