package com.emmsale.member.application.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InterestTagDeleteRequest {

  private final List<Long> tagIds;

  private InterestTagDeleteRequest() {
    this(null);
  }

}
