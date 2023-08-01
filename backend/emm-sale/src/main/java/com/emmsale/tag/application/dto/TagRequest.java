package com.emmsale.tag.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TagRequest {

  private final String name;

  @JsonCreator
  public TagRequest(@JsonProperty final String name) {
    this.name = name;
  }
}
