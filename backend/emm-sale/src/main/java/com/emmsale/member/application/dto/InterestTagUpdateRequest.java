package com.emmsale.member.application.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InterestTagUpdateRequest {

  private final List<Long> tagIds;

  private InterestTagUpdateRequest() {
    this(null);
  }
}
