package com.emmsale.block.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.emmsale.block.application.dto.BlockRequest;
import com.emmsale.block.domain.Block;
import com.emmsale.block.domain.BlockRepository;
import com.emmsale.block.exception.BlockException;
import com.emmsale.block.exception.BlockExceptionType;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BlockCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private BlockCommandService blockCommandService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private BlockRepository blockRepository;

  @Nested
  class RegisterTest {

    final Member requestMember = memberRepository.findById(1L).get();
    final Member blockMember = memberRepository.findById(2L).get();

    @Test
    @DisplayName("사용자를 성공적으로 차단한다.")
    void registerTest() {
      //given
      final BlockRequest blockRequest = new BlockRequest(blockMember.getId());

      //when
      blockCommandService.register(requestMember, blockRequest);

      //then
      assertTrue(
          blockRepository.existsByRequestMemberIdAndBlockMemberId(requestMember.getId(),
              blockMember.getId())
      );
    }

    @Test
    @DisplayName("이미 차단한 사용자일 경우 ALREADY_BLOCKED_MEMBER 타입의 Exception이 발생한다.")
    void registerWithAlreadyBlockedMember() {
      //given
      final BlockRequest blockRequest = new BlockRequest(blockMember.getId());
      final BlockExceptionType expectException = BlockExceptionType.ALREADY_BLOCKED_MEMBER;
      final Block block = new Block(requestMember, blockMember);
      blockRepository.save(block);

      //when
      final BlockException actualException = assertThrowsExactly(
          BlockException.class,
          () -> blockCommandService.register(requestMember, blockRequest)
      );

      //then
      assertEquals(expectException, actualException.exceptionType());
    }

    @Test
    @DisplayName("차단할 사용자를 찾을 수 없을 경우 NOT_FOUND_MEMBER 타입의 Exception이 발생한다.")
    void registerWithNotExistsUser() {
      //given
      final long notExistsMemberId = 0L;
      final BlockRequest blockRequest = new BlockRequest(notExistsMemberId);
      final BlockExceptionType expectExceptionType = BlockExceptionType.NOT_FOUND_MEMBER;

      //when
      final BlockException actualException = assertThrowsExactly(
          BlockException.class,
          () -> blockCommandService.register(requestMember, blockRequest)
      );

      //then
      assertEquals(expectExceptionType, actualException.exceptionType());
    }

    @Test
    @DisplayName("자기 자신을 차단할 경우 BAD_REQUEST_SELF_BLOCK 타입의 Exception이 발생한다.")
    void registerWithSelfBlockTest() {
      //given
      final BlockRequest blockRequest = new BlockRequest(requestMember.getId());
      final BlockExceptionType expectExceptionType = BlockExceptionType.BAD_REQUEST_SELF_BLOCK;

      //when
      final BlockException actualException = assertThrowsExactly(
          BlockException.class,
          () -> blockCommandService.register(requestMember, blockRequest)
      );

      //then
      assertEquals(expectExceptionType, actualException.exceptionType());
    }
  }
}
