package com.emmsale.notification.application.generator;

import com.emmsale.member.domain.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.format.DateTimeFormatter;

public interface NotificationMessageGenerator {

  boolean DEFAULT_VALIDATE_ONLY = false;
  DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss");

  String makeMessage(
      final String targetToken,
      final ObjectMapper objectMapper,
      final MemberRepository memberRepository
  );
}
