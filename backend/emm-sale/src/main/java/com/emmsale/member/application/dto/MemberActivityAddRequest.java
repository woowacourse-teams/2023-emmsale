package com.emmsale.member.application.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberActivityAddRequest {

  private final List<Long> activityIds;

  private MemberActivityAddRequest() {
    this(null);
  }
}
