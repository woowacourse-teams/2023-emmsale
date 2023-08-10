package com.emmsale.member.application.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InterestTagRequest {

  private final List<Long> tagIds;

  private InterestTagRequest() {
    this(null);
  }
}
