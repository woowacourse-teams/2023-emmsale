package com.emmsale.admin.login.api;

import com.emmsale.admin.login.application.AdminLoginService;
import com.emmsale.admin.login.application.dto.AdminLoginRequest;
import com.emmsale.admin.login.application.dto.AdminTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/login")
public class AdminLoginApi {

  private final AdminLoginService adminLoginService;

  public AdminLoginApi(final AdminLoginService adminLoginService) {
    this.adminLoginService = adminLoginService;
  }

  @PostMapping
  public ResponseEntity<AdminTokenResponse> login(@RequestBody final AdminLoginRequest request) {
    return ResponseEntity.ok().body(adminLoginService.createAdminToken(request));
  }
}
