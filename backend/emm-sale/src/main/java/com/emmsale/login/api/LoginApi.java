package com.emmsale.login.api;

import com.emmsale.login.application.LoginService;
import com.emmsale.login.application.dto.TokenResponse;
import com.emmsale.login.exception.LoginException;
import com.emmsale.login.exception.LoginExceptionType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginApi {

  private final LoginService loginService;

  public LoginApi(final LoginService loginService) {
    this.loginService = loginService;
  }

  @PostMapping("/github/callback")
  public ResponseEntity<TokenResponse> login(@RequestParam final String code) {
    if (code == null) {
      throw new LoginException(LoginExceptionType.NOT_FOUND_GITHUB_CODE);
    }
    return ResponseEntity.ok().body(loginService.createToken(code));
  }
}
