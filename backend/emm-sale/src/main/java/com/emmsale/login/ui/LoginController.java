package com.emmsale.login.ui;

import com.emmsale.login.application.LoginService;
import com.emmsale.login.application.dto.TokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

  private final LoginService loginService;

  public LoginController(final LoginService loginService) {
    this.loginService = loginService;
  }

  @GetMapping("/github/callback")
  public ResponseEntity<TokenResponse> login(@RequestParam final String code) {
    if (code == null) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok().body(loginService.createToken(code));
  }
}
