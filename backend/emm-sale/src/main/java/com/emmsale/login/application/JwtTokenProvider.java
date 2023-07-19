package com.emmsale.login.application;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value("${security.jwt.token.secret-key}")
  private String secretKey;
  @Value("${security.jwt.token.expire-length}")
  private long additionalValidityMillisecond;

  public String createToken(final String memberId) {
    final Claims claims = Jwts.claims().setSubject(memberId);
    final Date now = new Date();
    final Date expirationTime = new Date(now.getTime() + additionalValidityMillisecond);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expirationTime)
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }
}
