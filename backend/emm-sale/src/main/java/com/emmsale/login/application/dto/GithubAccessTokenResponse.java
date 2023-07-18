package com.emmsale.login.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GithubAccessTokenResponse {

  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("token_type")
  private String tokenType;
  private String scope;
  private String bearer;
}
