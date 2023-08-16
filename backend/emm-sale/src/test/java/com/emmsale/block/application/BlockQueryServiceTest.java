package com.emmsale.block.application;

import static com.emmsale.member.MemberFixture.memberFixture;
import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.block.application.dto.BlockResponse;
import com.emmsale.block.domain.Block;
import com.emmsale.block.domain.BlockRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BlockQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private BlockRepository blockRepository;
  @Autowired
  private BlockQueryService queryService;
  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("사용자의 차단 목록 전체 조회할 수 있다.")
  void findAllTest() {
    //given
    final Member requestMember = memberRepository.findById(1L).get();
    final Member blockMember_1 = memberRepository.findById(2L).get();
    final Member blockMember_2 = memberRepository.save(memberFixture());

    final Block block1 = blockRepository.save(new Block(1L, blockMember_1.getId()));
    final Block block2 = blockRepository.save(new Block(1L, blockMember_2.getId()));

    final List<BlockResponse> expected = List.of(
        new BlockResponse(block1.getId(), blockMember_1.getId(), blockMember_1.getImageUrl(),
            blockMember_1.getName()),
        new BlockResponse(block2.getId(), blockMember_2.getId(), blockMember_2.getImageUrl(),
            blockMember_2.getName())
    );

    //when
    final List<BlockResponse> actual = queryService.findAll(requestMember);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(expected);
  }
}
