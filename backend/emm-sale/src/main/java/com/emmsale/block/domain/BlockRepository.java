package com.emmsale.block.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {

  boolean existsByRequestMemberIdAndBlockMemberId(final Long requestMemberId,
      final Long blockMemberId);

  List<Block> findAllByRequestMemberId(final Long requestMemberId);
}
