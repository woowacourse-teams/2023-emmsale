package com.emmsale.admin.login.application;

import com.emmsale.admin.login.application.dto.AdminLoginRequest;
import com.emmsale.admin.login.application.dto.AdminTokenResponse;
import com.emmsale.login.exception.LoginException;
import com.emmsale.login.exception.LoginExceptionType;
import com.emmsale.login.utils.JwtTokenProvider;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminLoginService {

  private final JwtTokenProvider tokenProvider;
  @Value("${data.admin_login.id}")
  private String adminId;
  @Value("${data.admin_login.password}")
  private String adminPassword;
  @Value("${data.admin_login.member_id}")
  private Long adminMemberId;

  public AdminTokenResponse createAdminToken(final AdminLoginRequest request) {
    validateNotNullRequest(request);
    validateAdminLoginInformation(request);
    final String accessToken = tokenProvider.createToken(String.valueOf(adminMemberId));

    return new AdminTokenResponse(accessToken);
  }

  private void validateNotNullRequest(final AdminLoginRequest request) {
    if (request == null) {
      throw new LoginException(LoginExceptionType.NOT_FOUND_ADMIN_LOGIN_INFORMATION);
    }
  }

  private void validateAdminLoginInformation(final AdminLoginRequest request) {
    if (!(request.getId().equals(adminId) && request.getPassword().equals(adminPassword))) {
      throw new LoginException(LoginExceptionType.INVALID_ADMIN_LOGIN_INFORMATION);
    }
  }
}
