package com.emmsale.member.domain;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.helper.JpaRepositorySliceTestHelper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberActivityRepositoryTest extends JpaRepositorySliceTestHelper {

  @Autowired
  private MemberActivityRepository memberActivityRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("사용자를 통해 사용자의 Activity들을 모두 조회할 수 있다.")
  void findAllByMemberId() throws Exception {
    //given
    final Long memberId = 1L;
    final Member member = memberRepository.findById(memberId).get();

    //when
    final List<MemberActivity> memberActivities = memberActivityRepository.findAllByMember(member);

    //then
    final List<Long> memberActivityIds = memberActivities.stream()
        .map(MemberActivity::getId)
        .collect(toUnmodifiableList());

    System.out.println("memberActivityIds.size() = " + memberActivityIds.size());

    assertThat(memberActivityIds).containsExactlyInAnyOrder(1L, 2L, 3L);
  }

  @Test
  @DisplayName("사용자와 activity의 id를 통해서 사용자의 Activity들을 모두 조회할 수 있다.")
  void test_findAllByMemberAndActivityIds() throws Exception {
    //given
    final Long memberId = 1L;
    final List<Long> activityIds = List.of(1L, 2L);

    final Member member = memberRepository.findById(memberId).get();

    //when
    final List<MemberActivity> memberActivities =
        memberActivityRepository.findAllByMemberAndActivityIds(member, activityIds);

    //then
    final List<Long> memberActivityIds = memberActivities.stream()
        .map(MemberActivity::getId)
        .collect(toUnmodifiableList());

    assertThat(memberActivityIds).containsExactlyInAnyOrder(1L, 2L);
  }
}
