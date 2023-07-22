package com.emmsale.member.application;

import com.emmsale.login.application.dto.GithubProfileResponse;
import com.emmsale.login.application.dto.MemberQueryResponse;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.Optional;
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

  //TODO: 다른 클래스로 메서드 분리
  @Transactional
  public MemberQueryResponse findOrCreateMember(
      final GithubProfileResponse githubProfileFromGithub) {
    final Optional<Member> optionalMember = memberRepository.findByGithubId(
        githubProfileFromGithub.getGithubId());
    if (optionalMember.isPresent()) {
      final Member member = optionalMember.get();
      return new MemberQueryResponse(member.getId(), false);
    }
    final Member member = memberRepository.save(githubProfileFromGithub.toMember());
    return new MemberQueryResponse(member.getId(), true);
  }
}
