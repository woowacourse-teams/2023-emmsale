package com.emmsale.login.application.dto;

import com.emmsale.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

  public Member toMember() {
    return new Member(
        getGithubId(),
        getImageUrl(),
        getUsername()
    );
  }
}
