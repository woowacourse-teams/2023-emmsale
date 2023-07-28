package com.emmsale.login.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenResponse {

  private long memberId;
  private boolean isOnboarded;
  private String accessToken;
}
