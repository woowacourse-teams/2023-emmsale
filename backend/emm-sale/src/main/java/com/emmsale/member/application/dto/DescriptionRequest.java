package com.emmsale.member.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DescriptionRequest {

  private String description;

  public DescriptionRequest(final String description) {
    this.description = description;
  }
}
