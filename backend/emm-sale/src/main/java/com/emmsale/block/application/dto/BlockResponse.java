package com.emmsale.block.application.dto;

import com.emmsale.block.domain.Block;
import com.emmsale.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BlockResponse {

  private final Long id;
  private final Long blockMemberId;
  private final String imageUrl;
  private final String memberName;

  public static BlockResponse from(final Block block, final Member member) {
    return new BlockResponse(
        block.getId(),
        member.getId(),
        member.getImageUrl(),
        member.getName()
    );
  }
}
