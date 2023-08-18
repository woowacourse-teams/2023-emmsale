package com.emmsale.block.application;

import static com.emmsale.block.exception.BlockExceptionType.FORBBIDEN_UNREGISTER_BLOCK;
import static com.emmsale.block.exception.BlockExceptionType.NOT_FOUND_BLOCK;
import static com.emmsale.member.MemberFixture.memberFixture;
import static org.assertj.core.api.Assertions.assertThat;
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
      final Block block = new Block(requestMember.getId(), blockMember.getId());
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

  @Nested
  @DisplayName("차단된 사용자를 해제할 수 있다.")
  class UnregisterTest {

    final Member requestMember = memberRepository.findById(1L).get();
    final Member blockMember = memberRepository.findById(2L).get();

    @Test
    @DisplayName("사용자를 성공적으로 차단 해제한다.")
    void success() {
      //given
      final Block block = blockRepository.save(
          new Block(requestMember.getId(), blockMember.getId()));

      //when
      blockCommandService.unregister(block.getId(), requestMember);

      //then
      assertThat(blockRepository.existsById(block.getId()))
          .isFalse();
    }

    @Test
    @DisplayName("차단한 ID가 존재하지 않으면, exception이 발생한다.")
    void notFound() {
      //given
      final long nonExistBlockId = 0L;

      //when
      final BlockException actualException = assertThrowsExactly(
          BlockException.class,
          () -> {
            blockCommandService.unregister(nonExistBlockId, requestMember);
          }
      );

      //then
      assertThat(actualException.getMessage())
          .isEqualTo(NOT_FOUND_BLOCK.errorMessage());
    }

    @Test
    @DisplayName("member가 실제로 block을 한 대상자가 아닌 경우 exception이 발생한다.")
    void forbbiden() {
      //given
      final Block block = blockRepository.save(
          new Block(requestMember.getId(), blockMember.getId()));
      final Member otherMember = memberRepository
          .save(memberFixture());

      //when
      final BlockException actualException = assertThrowsExactly(
          BlockException.class,
          () -> blockCommandService.unregister(block.getId(), otherMember)
      );

      //then
      assertThat(actualException.getMessage())
          .isEqualTo(FORBBIDEN_UNREGISTER_BLOCK.errorMessage());
    }
  }
}
