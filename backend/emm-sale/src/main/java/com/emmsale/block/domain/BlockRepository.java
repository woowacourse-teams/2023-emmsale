package com.emmsale.block.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {

  boolean existsByRequestMemberIdAndBlockMemberId(final Long requestMemberId,
      final Long blockMemberId);
}
