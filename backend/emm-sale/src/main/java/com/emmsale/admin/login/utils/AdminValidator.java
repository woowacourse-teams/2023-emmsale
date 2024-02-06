package com.emmsale.admin.login.utils;

import com.emmsale.login.exception.LoginException;
import com.emmsale.login.exception.LoginExceptionType;
import com.emmsale.member.domain.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminValidator {

  private static Long adminMemberId;

  public static void validateAuthorization(final Member admin) {
    if (admin.isNotMe(adminMemberId)) {
      throw new LoginException(LoginExceptionType.INVALID_ADMIN_ACCESS_TOKEN);
    }
  }

  @Value("${data.admin_login.member_id}")
  public void setAdminMemberId(final Long adminMemberId) {
    AdminValidator.adminMemberId = adminMemberId;
  }
}
