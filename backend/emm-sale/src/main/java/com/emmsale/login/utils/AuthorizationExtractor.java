package com.emmsale.login.utils;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

public class AuthorizationExtractor {

  private static final String ACCESS_TOKEN_TYPE =
      AuthorizationExtractor.class.getSimpleName() + ".ACCESS_TOKEN_TYPE";
  private static final String BEARER_TYPE = "Bearer";

  public static String extract(final HttpServletRequest request) {
    String authHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION)
        .substring(BEARER_TYPE.length()).trim();

    if (authHeaderValue.isBlank()) {
      return null;
    }

    request.setAttribute(ACCESS_TOKEN_TYPE, BEARER_TYPE);
    final int commaIndex = authHeaderValue.indexOf(',');
    if (commaIndex > 0) {
      authHeaderValue = authHeaderValue.substring(0, commaIndex);
    }
    return authHeaderValue;
  }
}
