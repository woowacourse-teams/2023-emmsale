package com.emmsale.member.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.emmsale.member.application.dto.MemberCareerInitialRequest;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberCareerServiceTest {

  @Autowired
  private MemberCareerService memberCareerService;

  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("커리어의 id를 통해서, 사용자의 커리어를 등록할 수 있다.")
  void registerCareer() throws Exception {
    //given
    final List<Long> careerIds = List.of(1L, 2L, 3L, 4L);
    final long savedMemberId = 1L;

    final Member member = memberRepository.findById(savedMemberId).get();
    final String updateName = "우르";

    final MemberCareerInitialRequest request = new MemberCareerInitialRequest(updateName, careerIds);

    //when & then
    assertAll(
        () -> assertDoesNotThrow(() -> memberCareerService.registerCareer(member, request)),
        () -> assertEquals(updateName, member.getName())
    );
  }
}
