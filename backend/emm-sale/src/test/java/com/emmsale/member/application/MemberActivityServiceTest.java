package com.emmsale.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.application.dto.MemberActivityAddRequest;
import com.emmsale.member.application.dto.MemberActivityDeleteRequest;
import com.emmsale.member.application.dto.MemberActivityInitialRequest;
import com.emmsale.member.application.dto.MemberActivityResponse;
import com.emmsale.member.application.dto.MemberActivityResponses;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberActivityServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MemberActivityService memberActivityService;

  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("Activity의 id를 통해서, 사용자의 Activity를 등록하고 사용자의 이름을 수정할 수 있다.")
  void registerActivities() throws Exception {
    //given
    final List<Long> activityIds = List.of(1L, 2L, 3L, 4L);
    final long savedMemberId = 1L;

    final Member member = memberRepository.findById(savedMemberId).get();
    final String updateName = "우르";

    final MemberActivityInitialRequest request = new MemberActivityInitialRequest(updateName,
        activityIds);

    //when & then
    assertAll(
        () -> assertDoesNotThrow(() -> memberActivityService.registerActivities(member, request)),
        () -> assertEquals(updateName, member.getName())
    );
  }

  @Test
  @DisplayName("Activity의 id를 통해서, 사용자의 Activity에 추가할 수 있다.")
  void addActivity() throws Exception {
    //given
    final List<Long> activityIds = List.of(4L, 5L, 6L);
    final long savedMemberId = 1L;

    final Member member = memberRepository.findById(savedMemberId).get();
    final MemberActivityAddRequest request = new MemberActivityAddRequest(activityIds);

    final List<MemberActivityResponses> expected = List.of(
        new MemberActivityResponses("동아리",
            List.of(
                new MemberActivityResponse(1L, "YAPP"),
                new MemberActivityResponse(2L, "DND"),
                new MemberActivityResponse(3L, "nexters")
            )),
        new MemberActivityResponses("컨퍼런스",
            List.of(
                new MemberActivityResponse(4L, "인프콘")
            )),
        new MemberActivityResponses("교육",
            List.of(
                new MemberActivityResponse(5L, "우아한테크코스")
            )),
        new MemberActivityResponses("직무",
            List.of(
                new MemberActivityResponse(6L, "Backend")
            ))
    );

    //when
    final List<MemberActivityResponses> actual = memberActivityService.addActivity(member, request);

    //then
    assertThat(expected)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(actual);
  }

  @Test
  @DisplayName("addActivity() : 유효하지 않은 activityId들이 있으면 invalid_activity_ids Exception이 발생합니다.")
  void test_addActivity_invalid_activity_ids_Exception() throws Exception {
    //given
    final Member savedMember = memberRepository.findById(1L).get();
    final List<Long> activityIds = List.of(1L, 2L, 7L);
    final MemberActivityAddRequest request = new MemberActivityAddRequest(activityIds);

    //when & then
    assertThatThrownBy(() -> memberActivityService.addActivity(savedMember, request))
        .isInstanceOf(MemberException.class)
        .hasMessage(MemberExceptionType.INVALID_ACTIVITY_IDS.errorMessage());
  }

  @Test
  @DisplayName("deleteActivity() : member Id와 삭제할 activity Id를 통해서 사용자의 Activity를 삭제할 수 있다.")
  void test_deleteActivity() throws Exception {
    //given
    final List<Long> deleteActivityIds = List.of(1L, 2L);
    final long savedMemberId = 1L;

    final Member member = memberRepository.findById(savedMemberId).get();
    final MemberActivityDeleteRequest request = new MemberActivityDeleteRequest(deleteActivityIds);

    final List<MemberActivityResponses> expected = List.of(
        new MemberActivityResponses("동아리",
            List.of(
                new MemberActivityResponse(3L, "nexters")
            ))
    );

    //when
    final List<MemberActivityResponses> actual = memberActivityService.deleteActivity(member,
        request);

    //then
    assertThat(expected)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(actual);
  }
}
