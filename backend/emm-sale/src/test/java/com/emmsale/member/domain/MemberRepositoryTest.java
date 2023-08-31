package com.emmsale.member.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.emmsale.helper.JpaRepositorySliceTestHelper;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberRepositoryTest extends JpaRepositorySliceTestHelper {

  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("countMembersById() : 주어진 ids에서 존재하는 members의 개수를 구할 수 있다.")
  void test_existsMembersByInIds() throws Exception {
    //given
    final List<Long> memberIds = List.of(1L, 2L, 3L);

    //when
    final Long count = memberRepository.countMembersById(memberIds);

    //then
    assertEquals(2, count);
  }

  @Test
  @DisplayName("findAllByIdIn() : 주어진 id들에 속한 member들을 조회할 수 있다.")
  void test_findAllByIdIn() throws Exception {
    //given
    final Set<Long> memberIds = Set.of(1L, 2L, 3L);

    final List<Member> expected = List.of(
        memberRepository.findById(1L).get(),
        memberRepository.findById(2L).get()
    );

    //when
    final List<Member> actual = memberRepository.findAllByIdIn(memberIds);

    //then
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt")
        .isEqualTo(expected);
  }
}
