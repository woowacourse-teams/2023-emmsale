package com.emmsale.member.domain;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MemberActivityRepositoryTest {

  @Autowired
  private MemberActivityRepository memberActivityRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("사용자를 통해 사용자의 커리어들을 모두 조회할 수 있다.")
  void findAllByMemberId() throws Exception {
    //given
    final Long memberId = 1L;
    final Member member = memberRepository.findById(memberId).get();

    //when
    final List<MemberActivity> memberActivities = memberActivityRepository.findAllByMember(member);

    //then
    final List<Long> memberCareerIds = memberActivities.stream()
        .map(MemberActivity::getId)
        .collect(toUnmodifiableList());

    assertThat(memberCareerIds).containsExactlyInAnyOrder(1L, 2L, 3L);
  }

  @Test
  @DisplayName("사용자와 career의 id를 통해서 사용자의 커리어들을 모두 조회할 수 있다.")
  void test_findAllByMemberAndCareerIds() throws Exception {
    //given
    final Long memberId = 1L;
    final List<Long> careerIds = List.of(1L, 2L);

    final Member member = memberRepository.findById(memberId).get();

    //when
    final List<MemberActivity> memberActivities =
        memberActivityRepository.findAllByMemberAndCareerIds(member, careerIds);

    //then
    final List<Long> memberCareerIds = memberActivities.stream()
        .map(MemberActivity::getId)
        .collect(toUnmodifiableList());

    assertThat(memberCareerIds).containsExactlyInAnyOrder(1L, 2L);
  }
}
