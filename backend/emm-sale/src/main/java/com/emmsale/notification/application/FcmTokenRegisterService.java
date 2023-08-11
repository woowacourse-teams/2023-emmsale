package com.emmsale.notification.application;

import static com.emmsale.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.notification.application.dto.FcmTokenRequest;
import com.emmsale.notification.domain.FcmToken;
import com.emmsale.notification.domain.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmTokenRegisterService {

  private final FcmTokenRepository fcmTokenRepository;
  private final MemberRepository memberRepository;

  public void registerFcmToken(final FcmTokenRequest fcmTokenRequest) {
    final Long memberId = fcmTokenRequest.getMemberId();
    final String token = fcmTokenRequest.getToken();

    validateExistedMember(memberId);

    fcmTokenRepository.findByMemberId(memberId)
        .ifPresentOrElse(
            it -> it.update(token),
            () -> fcmTokenRepository.save(new FcmToken(token, memberId))
        );
  }

  private void validateExistedMember(final Long memberId) {
    if (!memberRepository.existsById(memberId)) {
      throw new MemberException(NOT_FOUND_MEMBER);
    }
  }
}
