package com.emmsale.admin.login.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdminLoginRequest {

  final String id;
  final String password;

}
