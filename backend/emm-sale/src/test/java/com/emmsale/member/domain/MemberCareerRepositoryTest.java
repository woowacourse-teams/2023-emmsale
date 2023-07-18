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
class MemberCareerRepositoryTest {

  @Autowired
  private MemberCareerRepository memberCareerRepository;

  @Test
  @DisplayName("사용자의 id를 통해 사용자의 커리어들을 모두 조회할 수 있다.")
  void findAllByMemberId() throws Exception {
    //given
    final Long memberId = 1L;

    //when
    final List<MemberCareer> memberCareers = memberCareerRepository.findAllByMemberId(memberId);

    //then
    final List<Long> memberCareerIds = memberCareers.stream()
        .map(MemberCareer::getId)
        .collect(toUnmodifiableList());

    assertThat(memberCareerIds).containsExactlyInAnyOrder(1L, 2L, 3L);
  }
}
