package com.emmsale.member.application;

import com.emmsale.member.application.dto.OpenProfileUrlRequest;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
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
    final Member persistMember = memberRepository.findById(member.getId()).get();
    final String openProfileUrl = openProfileUrlRequest.getOpenProfileUrl();

    persistMember.updateOpenProfileUrl(openProfileUrl);
  }
}
