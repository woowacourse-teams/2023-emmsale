package com.emmsale.member.application;

import static com.emmsale.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import com.emmsale.login.application.dto.GithubProfileResponse;
import com.emmsale.login.application.dto.MemberQueryResponse;
import com.emmsale.member.application.dto.MemberDetailResponse;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberActivity;
import com.emmsale.member.domain.MemberActivityRepository;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

  private final MemberRepository memberRepository;
  private final MemberActivityRepository memberActivityRepository;

  public Member findById(final Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
  }

  //TODO: 다른 클래스로 메서드 분리
  @Transactional
  public MemberQueryResponse findOrCreateMember(
      final GithubProfileResponse githubProfileFromGithub
  ) {
    final Member member = memberRepository.findByOauthId(githubProfileFromGithub.getGithubId())
        .orElseGet(() -> memberRepository.save(githubProfileFromGithub.toMember()));
    return new MemberQueryResponse(member.getId(), member.isOnboarded());
  }

  public MemberDetailResponse findProfile(final Long memberId) {
    final Member member = memberRepository.getByIdOrElseThrow(memberId);
    final List<MemberActivity> memberActivities = memberActivityRepository.findAllByMember(member);

    return MemberDetailResponse.of(member, memberActivities);
  }
}
