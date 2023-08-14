package com.emmsale.tag.application.dto;

import com.emmsale.tag.domain.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TagResponse {

  private final Long id;
  private final String name;

  public static TagResponse from(final Tag tag) {
    return new TagResponse(tag.getId(), tag.getName());
  }
}
