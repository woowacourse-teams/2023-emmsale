package com.emmsale.tag.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TagResponse {

  private final Long id;
  private final String name;
}
