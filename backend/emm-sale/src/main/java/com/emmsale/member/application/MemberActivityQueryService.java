package com.emmsale.member.application;

import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.activity.application.dto.ActivityResponse;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberActivity;
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

  public List<ActivityResponse> findActivities(final Long memberId) {
    final Member member = memberRepository.getByIdOrElseThrow(memberId);

    return memberActivityRepository.findAllByMember(member)
        .stream()
        .map(MemberActivity::getActivity)
        .map(ActivityResponse::from)
        .collect(toUnmodifiableList());
  }
}
