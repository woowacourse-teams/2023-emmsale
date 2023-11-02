package com.emmsale.member.application;

import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.member.application.dto.MemberActivityResponse;
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

  public List<MemberActivityResponse> findActivities(final Long memberId) {
    final Member member = memberRepository.getByIdOrElseThrow(memberId);

    return memberActivityRepository.findAllByMember(member)
        .stream()
        .map(MemberActivityResponse::from)
        .collect(toUnmodifiableList());
  }
}
