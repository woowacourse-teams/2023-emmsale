package com.emmsale.admin.activity.application;

import static com.emmsale.member.MemberFixture.adminMember;
import static com.emmsale.member.MemberFixture.generalMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.activity.application.dto.ActivityAddRequest;
import com.emmsale.activity.application.dto.ActivityResponse;
import com.emmsale.activity.domain.ActivityType;
import com.emmsale.activity.exception.ActivityException;
import com.emmsale.activity.exception.ActivityExceptionType;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.login.exception.LoginException;
import com.emmsale.login.exception.LoginExceptionType;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ActivityCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private ActivityCommandService activityCommandService;

  @Test
  @DisplayName("새로운 활동을 추가할 수 있다.")
  void findActivity() {
    //given
    final String activityName = "DDD";
    final ActivityAddRequest request = new ActivityAddRequest(ActivityType.CLUB, activityName);
    final ActivityResponse expected = new ActivityResponse(
        7L,
        ActivityType.CLUB.getValue(),
        activityName
    );

    //when
    final ActivityResponse actual
        = activityCommandService.addActivity(request, adminMember());

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("이미 존재하는 활동을 추가하면 예외를 반환한다.")
  void findActivity_fail_duplicate() {
    //given
    final String activityName = "DDD";
    final ActivityAddRequest request = new ActivityAddRequest(ActivityType.CLUB, activityName);
    activityCommandService.addActivity(request, adminMember());

    //when
    final ThrowingCallable actual = () -> activityCommandService.addActivity(request,
        adminMember());

    //then
    assertThatThrownBy(actual)
        .isInstanceOf(ActivityException.class)
        .hasMessage(ActivityExceptionType.ALEADY_EXIST_ACTIVITY.errorMessage());
  }

  @Test
  @DisplayName("관리자가 아닌 회원이 활동을 추가하면 예외를 반환한다.")
  void findActivity_fail_authorization() {
    //given
    final String activityName = "DDD";
    final ActivityAddRequest request = new ActivityAddRequest(ActivityType.CLUB, activityName);

    //when
    final ThrowingCallable actual = () -> activityCommandService.addActivity(request,
        generalMember());

    //then
    assertThatThrownBy(actual)
        .isInstanceOf(LoginException.class)
        .hasMessage(LoginExceptionType.INVALID_ACCESS_TOKEN.errorMessage());
  }
}
