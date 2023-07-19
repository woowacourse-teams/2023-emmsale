package com.emmsale.member.application;

import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

  private final MemberRepository memberRepository;

  public Member findById(final Long memberId) {
    return memberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);
  }
}
