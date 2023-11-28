package com.emmsale.admin.login.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AdminTokenResponse {

  private final String accessToken;
}
