package com.emmsale.notification.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FcmTokenRequest {

  private final String token;
  private final Long memberId;
}
