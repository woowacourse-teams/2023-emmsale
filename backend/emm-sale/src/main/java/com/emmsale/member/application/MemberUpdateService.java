package com.emmsale.member.application;

import com.emmsale.member.application.dto.DescriptionRequest;
import com.emmsale.member.application.dto.OpenProfileUrlRequest;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberUpdateService {

  private final MemberRepository memberRepository;

  public void updateOpenProfileUrl(
      final Member member,
      final OpenProfileUrlRequest openProfileUrlRequest
  ) {
    final Member persistMember = memberRepository.findById(member.getId())
        .orElseThrow(() -> new MemberException((MemberExceptionType.NOT_FOUND_MEMBER)));
    final String openProfileUrl = openProfileUrlRequest.getOpenProfileUrl();

    persistMember.updateOpenProfileUrl(openProfileUrl);
  }

  public void updateDescription(final Member member, final DescriptionRequest descriptionRequest) {
    final String description = descriptionRequest.getDescription();

    final Member persistMember = memberRepository.findById(member.getId())
        .orElseThrow(() -> new MemberException((MemberExceptionType.NOT_FOUND_MEMBER)));
    persistMember.updateDescription(description);
  }

  public void deleteMember(final Member member, final Long memberId) {
    if (member.isNotMe(memberId)) {
      throw new MemberException(MemberExceptionType.FORBIDDEN_DELETE_MEMBER);
    }

    memberRepository.deleteById(memberId);
  }
}
