package com.emmsale.login.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GithubProfileResponse {

  @JsonProperty("id")
  private String githubId;
  @JsonProperty("name")
  private String name;
  @JsonProperty("login")
  private String username;
  @JsonProperty("avatar_url")
  private String imageUrl;

  public Long getGithubId() {
    return Long.valueOf(githubId);
  }
}
