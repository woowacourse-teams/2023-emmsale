package com.emmsale.member.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql("/data-test.sql")
class MemberRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("existsMembersByInIds() : 주어진 ids에서 하나라도 존재하지 않는 id가 있다면 false를 반환할 수 있다.")
  void test_existsMembersByInIds() throws Exception {
    //given
    final List<Long> memberIds = List.of(1L, 2L, 3L);

    //when
    final Long count = memberRepository.countMembersById(memberIds);

    //then
    assertEquals(2, count);
  }
}
