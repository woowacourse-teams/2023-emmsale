package com.emmsale.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.application.dto.MemberActivityResponse;
import com.emmsale.member.application.dto.MemberCareerAddRequest;
import com.emmsale.member.application.dto.MemberCareerDeleteRequest;
import com.emmsale.member.application.dto.MemberCareerInitialRequest;
import com.emmsale.member.application.dto.MemberCareerResponse;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberCareerServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MemberCareerService memberCareerService;

  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("커리어의 id를 통해서, 사용자의 커리어를 등록하고 사용자의 이름을 수정할 수 있다.")
  void registerCareer() throws Exception {
    //given
    final List<Long> careerIds = List.of(1L, 2L, 3L, 4L);
    final long savedMemberId = 1L;

    final Member member = memberRepository.findById(savedMemberId).get();
    final String updateName = "우르";

    final MemberCareerInitialRequest request = new MemberCareerInitialRequest(updateName,
        careerIds);

    //when & then
    assertAll(
        () -> assertDoesNotThrow(() -> memberCareerService.registerCareer(member, request)),
        () -> assertEquals(updateName, member.getName())
    );
  }

  @Test
  @DisplayName("커리어의 id를 통해서, 사용자의 커리어에 추가할 수 있다.")
  void addCareer() throws Exception {
    //given
    final List<Long> careerIds = List.of(4L, 5L, 6L);
    final long savedMemberId = 1L;

    final Member member = memberRepository.findById(savedMemberId).get();
    final MemberCareerAddRequest request = new MemberCareerAddRequest(careerIds);

    final List<MemberCareerResponse> expected = List.of(
        new MemberCareerResponse("동아리",
            List.of(
                new MemberActivityResponse(1L, "YAPP"),
                new MemberActivityResponse(2L, "DND"),
                new MemberActivityResponse(3L, "nexters")
            )),
        new MemberCareerResponse("컨퍼런스",
            List.of(
                new MemberActivityResponse(4L, "인프콘")
            )),
        new MemberCareerResponse("교육",
            List.of(
                new MemberActivityResponse(5L, "우아한테크코스")
            )),
        new MemberCareerResponse("직무",
            List.of(
                new MemberActivityResponse(6L, "Backend")
            ))
    );

    //when
    final List<MemberCareerResponse> actual = memberCareerService.addCareer(member, request);

    //then
    assertThat(expected)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(actual);
  }

  @Test
  @DisplayName("deleteCareer() : member Id와 삭제할 career Id를 통해서 사용자의 커리어를 삭제할 수 있다.")
  void test_deleteCareer() throws Exception {
    //given
    final List<Long> deleteCareerIds = List.of(1L, 2L);
    final long savedMemberId = 1L;

    final Member member = memberRepository.findById(savedMemberId).get();
    final MemberCareerDeleteRequest request = new MemberCareerDeleteRequest(deleteCareerIds);

    final List<MemberCareerResponse> expected = List.of(
        new MemberCareerResponse("동아리",
            List.of(
                new MemberActivityResponse(3L, "nexters")
            ))
    );

    //when
    final List<MemberCareerResponse> actual = memberCareerService.deleteCareer(member, request);

    //then
    assertThat(expected)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(actual);
  }
}
