package com.emmsale.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.emmsale.activity.application.dto.ActivityResponse;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.application.dto.MemberActivityAddRequest;
import com.emmsale.member.application.dto.MemberActivityInitialRequest;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberActivityCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MemberActivityCommandService memberActivityCommandService;

  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("Activity의 id를 통해서, 사용자의 Activity를 등록하고 사용자의 이름을 수정할 수 있다.")
  void registerActivities() throws Exception {
    //given
    final List<Long> activityIds = List.of(1L, 2L, 3L);
    final long savedMemberId = 1L;

    final Member member = memberRepository.findById(savedMemberId).get();
    final String updateName = "우르";

    final MemberActivityInitialRequest request = new MemberActivityInitialRequest(updateName,
        activityIds);

    //when & then
    assertAll(
        () -> assertDoesNotThrow(
            () -> memberActivityCommandService.registerActivities(member, request)),
        () -> assertEquals(updateName, member.getName())
    );
  }

  @Test
  @DisplayName("특정 사용자에 대해 이미 등록되어 있는 Activity를 등록하려고 하면 예외를 반환한다.")
  void registerActivities_fail() throws Exception {
    //given
    final List<Long> activityIds = List.of(1L, 2L, 3L, 4L);
    final long savedMemberId = 1L;

    final Member member = memberRepository.findById(savedMemberId).get();
    final String updateName = "우르";

    final MemberActivityInitialRequest request = new MemberActivityInitialRequest(updateName,
        activityIds);

    // when
    memberActivityCommandService.registerActivities(member, request);
    final ThrowingCallable actual = () -> memberActivityCommandService.registerActivities(member,
        request);

    // then
    assertThatThrownBy(actual)
        .isInstanceOf(MemberException.class)
        .hasMessage(MemberExceptionType.ALREADY_ONBOARDING.errorMessage());

  }

  @Test
  @DisplayName("Activity의 id를 통해서, 사용자의 Activity에 추가할 수 있다.")
  void addActivity() {
    //given
    final List<Long> activityIds = List.of(4L, 5L, 6L);
    final long savedMemberId = 1L;

    final Member member = memberRepository.findById(savedMemberId).get();
    final MemberActivityAddRequest request = new MemberActivityAddRequest(activityIds);

    final List<ActivityResponse> expected = List.of(
        new ActivityResponse(1L, "동아리", "YAPP"),
        new ActivityResponse(2L, "동아리", "DND"),
        new ActivityResponse(3L, "동아리", "nexters"),
        new ActivityResponse(4L, "컨퍼런스", "인프콘"),
        new ActivityResponse(5L, "교육", "우아한테크코스"),
        new ActivityResponse(6L, "직무", "Backend")
    );

    //when
    final List<ActivityResponse> actual = memberActivityCommandService.addActivity(member,
        request);
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
    final List<Long> activityIds = List.of(4L, 5L, 7L);
    final MemberActivityAddRequest request = new MemberActivityAddRequest(activityIds);

    //when & then
    assertThatThrownBy(() -> memberActivityCommandService.addActivity(savedMember, request))
        .isInstanceOf(MemberException.class)
        .hasMessage(MemberExceptionType.INVALID_ACTIVITY_IDS.errorMessage());
  }

  @Test
  @DisplayName("addActivity() : 이미 존재하는 activityId들이 있으면 ALREADY_EXIST_ACTIVITY Exception이 발생합니다.")
  void test_addActivity_ALREADY_EXIST_ACTIVITY_Exception_duplicate_try() throws Exception {
    //given
    final Member savedMember = memberRepository.findById(1L).get();
    final List<Long> activityIds = List.of(1L, 2L, 7L);
    final MemberActivityAddRequest request = new MemberActivityAddRequest(activityIds);

    // when, then
    assertThatThrownBy(() -> memberActivityCommandService.addActivity(savedMember, request))
        .isInstanceOf(MemberException.class)
        .hasMessage(MemberExceptionType.ALREADY_EXIST_ACTIVITY.errorMessage());
  }

  @Test
  @DisplayName("addActivity() : 중복되는 Activity Id가 포함되어 있으면 ALREADY_EXIST_ACTIVITY Exception이 발생합니다.")
  void test_addActivity_ALREADY_EXIST_ACTIVITY_Exception_duplicate_input() throws Exception {
    //given
    final Member savedMember = memberRepository.findById(1L).get();
    final List<Long> activityIds = List.of(4L, 4L, 5L);
    final MemberActivityAddRequest request = new MemberActivityAddRequest(activityIds);

    // when, then
    assertThatThrownBy(() -> memberActivityCommandService.addActivity(savedMember, request))
        .isInstanceOf(MemberException.class)
        .hasMessage(MemberExceptionType.DUPLICATE_ACTIVITY.errorMessage());
  }

  @Test
  @DisplayName("deleteActivity() : member Id와 삭제할 activity Id를 통해서 사용자의 Activity를 삭제할 수 있다.")
  void test_deleteActivity() throws Exception {
    //given
    final List<Long> deleteActivityIds = List.of(1L, 2L);
    final long savedMemberId = 1L;

    final Member member = memberRepository.findById(savedMemberId).get();

    final List<ActivityResponse> expected = List.of(
        new ActivityResponse(3L, "동아리", "nexters")
    );

    //when
    final List<ActivityResponse> actual = memberActivityCommandService.deleteActivity(member,
        deleteActivityIds);

    //then
    assertThat(expected)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(actual);
  }
}
