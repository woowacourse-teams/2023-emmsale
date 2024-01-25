package com.emmsale.login.application.dto;

import com.emmsale.login.domain.OAuthProfileResponse;
import com.emmsale.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class GithubProfileResponse implements OAuthProfileResponse {

  @JsonProperty("id")
  private String githubId;
  @JsonProperty("name")
  private String name;
  @JsonProperty("login")
  private String username;
  @JsonProperty("avatar_url")
  private String imageUrl;

  //TODO : 추후 삭제
  public Long getGithubId() {
    return Long.valueOf(githubId);
  }

  @Override
  public Long getId() {
    return Long.valueOf(githubId);
  }

  @Override
  public Member toMember() {
    return new Member(
        getGithubId(),
        getImageUrl(),
        getUsername()
    );
  }
}
