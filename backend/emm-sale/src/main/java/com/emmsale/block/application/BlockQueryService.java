package com.emmsale.block.application;

import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.block.application.dto.BlockResponse;
import com.emmsale.block.domain.Block;
import com.emmsale.block.domain.BlockRepository;
import com.emmsale.block.exception.BlockException;
import com.emmsale.block.exception.BlockExceptionType;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BlockQueryService {

  private final BlockRepository blockRepository;
  private final MemberRepository memberRepository;

  public List<BlockResponse> findAll(final Member member) {
    return blockRepository.findAllByRequestMemberId(member.getId())
        .stream()
        .map(this::convertTo)
        .collect(toUnmodifiableList());
  }

  private BlockResponse convertTo(final Block block) {
    final Member member = memberRepository.findById(block.getBlockMemberId())
        .orElseThrow(() -> new BlockException(BlockExceptionType.NOT_FOUND_MEMBER));
    return BlockResponse.from(block, member);
  }
}
