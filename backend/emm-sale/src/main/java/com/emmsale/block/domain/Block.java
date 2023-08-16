package com.emmsale.block.domain;

import com.emmsale.base.BaseEntity;
import com.emmsale.block.exception.BlockException;
import com.emmsale.block.exception.BlockExceptionType;
import com.emmsale.member.domain.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private Long requestMemberId;
  @Column(nullable = false)
  private Long blockMemberId;

  public Block(final Long requestMemberId, final Long blockMemberId) {
    this.requestMemberId = requestMemberId;
    this.blockMemberId = blockMemberId;
  }

  public void validateOwner(final Member member) {
    if (member.isNotMe(requestMemberId)) {
      throw new BlockException(BlockExceptionType.FORBBIDEN_UNREGISTER_BLOCK);
    }
  }
}
