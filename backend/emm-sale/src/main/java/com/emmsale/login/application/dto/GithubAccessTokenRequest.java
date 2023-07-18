package com.emmsale.login.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GithubAccessTokenRequest {

  private final String code;
  @JsonProperty("client_id")
  private final String clientId;
  @JsonProperty("client_secret")
  private final String clientSecret;
}
