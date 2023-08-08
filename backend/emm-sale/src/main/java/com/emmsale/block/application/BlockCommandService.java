package com.emmsale.block.application;

import com.emmsale.block.application.dto.BlockRequest;
import com.emmsale.block.domain.Block;
import com.emmsale.block.domain.BlockRepository;
import com.emmsale.block.exception.BlockException;
import com.emmsale.block.exception.BlockExceptionType;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BlockCommandService {

  private final BlockRepository blockRepository;
  private final MemberRepository memberRepository;

  public void register(final Member requestMember, final BlockRequest blockRequest) {
    final Member blockMember = memberRepository.findById(blockRequest.getBlockMemberId())
        .orElseThrow(() -> new BlockException(BlockExceptionType.NOT_FOUND_MEMBER));

    validateSelfBlock(requestMember.getId(), blockMember.getId());
    validateAlreadyBlocked(requestMember.getId(), blockMember.getId());

    final Block block = new Block(requestMember, blockMember);
    blockRepository.save(block);
  }

  private void validateSelfBlock(final Long requestMemberId, final Long blockMemberId) {
    if (requestMemberId.equals(blockMemberId)) {
      throw new BlockException(BlockExceptionType.BAD_REQUEST_SELF_BLOCK);
    }
  }

  private void validateAlreadyBlocked(final Long requestMemberId, final Long blockMemberId) {
    if (blockRepository.existsByRequestMemberIdAndBlockMemberId(requestMemberId, blockMemberId)) {
      throw new BlockException(BlockExceptionType.ALREADY_BLOCKED_MEMBER);
    }
  }
}
