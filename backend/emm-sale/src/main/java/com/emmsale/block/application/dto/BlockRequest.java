package com.emmsale.block.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BlockRequest {

  private final Long blockMemberId;

  private BlockRequest() {
    this(null);
  }
}
