package com.emmsale.member.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class OpenProfileUrlRequest {

  @Pattern(regexp = "(https://open.kakao.com/).*")
  private final String openProfileUrl;

  @JsonCreator
  public OpenProfileUrlRequest(@JsonProperty final String openProfileUrl) {
    this.openProfileUrl = openProfileUrl;
  }
}
