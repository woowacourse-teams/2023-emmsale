package com.emmsale.notification.application.generator;

import com.emmsale.member.domain.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface NotificationMessageGenerator {

  boolean DEFAULT_VALIDATE_ONLY = false;

  String makeMessage(
      final String targetToken,
      final ObjectMapper objectMapper,
      final MemberRepository memberRepository
  );
}
