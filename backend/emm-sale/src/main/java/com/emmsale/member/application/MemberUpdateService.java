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

  public static final int MAX_DESCRIPTION_LENGTH = 100;
  public static final String DEFAULT_DESCRIPTION = "";
  private final MemberRepository memberRepository;

  public void updateOpenProfileUrl(
      final Member member,
      final OpenProfileUrlRequest openProfileUrlRequest
  ) {
    final Member persistMember = memberRepository.findById(member.getId()).get();
    final String openProfileUrl = openProfileUrlRequest.getOpenProfileUrl();

    persistMember.updateOpenProfileUrl(openProfileUrl);
  }

  public void updateDescription(final Member member, final DescriptionRequest descriptionRequest) {
    String description = descriptionRequest.getDescription();
    validateDescription(description);

    final Member persistMember = memberRepository.findById(member.getId()).get();

    if (description.isBlank()) {
      persistMember.updateDescription(DEFAULT_DESCRIPTION);
      return;
    }
    persistMember.updateDescription(description);
  }

  private void validateDescription(final String description) {
    if (description.length() > MAX_DESCRIPTION_LENGTH) {
      throw new MemberException(MemberExceptionType.OVER_LENGTH_DESCRIPTION);
    }
  }
}
