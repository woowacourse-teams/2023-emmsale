package com.emmsale.login.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TokenResponse {

  private final long memberId;
  private final boolean onboarded;
  private final String accessToken;
}
