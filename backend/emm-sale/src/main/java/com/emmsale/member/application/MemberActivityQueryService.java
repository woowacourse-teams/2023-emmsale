package com.emmsale.member.application;

import com.emmsale.member.application.dto.MemberActivityResponses;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberActivityRepository;
import com.emmsale.member.domain.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberActivityQueryService {

  private final MemberRepository memberRepository;
  private final MemberActivityRepository memberActivityRepository;

  public List<MemberActivityResponses> findActivities(final Long memberId) {
    final Member member = memberRepository.getByIdOrElseThrow(memberId);
    return MemberActivityResponses.from(memberActivityRepository.findAllByMember(member));
  }
}
