package com.emmsale.admin.login.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminLoginRequest {

  private final String id;
  private final String password;

}
