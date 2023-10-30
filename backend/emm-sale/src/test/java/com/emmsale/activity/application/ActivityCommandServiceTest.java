package com.emmsale.activity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.activity.application.dto.ActivityAddRequest;
import com.emmsale.activity.application.dto.ActivityResponseRefactor;
import com.emmsale.activity.domain.ActivityType;
import com.emmsale.activity.exception.ActivityException;
import com.emmsale.activity.exception.ActivityExceptionType;
import com.emmsale.helper.ServiceIntegrationTestHelper;
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
    final ActivityResponseRefactor expected = new ActivityResponseRefactor(
      7L,
      ActivityType.CLUB.getValue(),
      activityName
    );

    //when
    final ActivityResponseRefactor actual
      = activityCommandService.addActivity(request);

    //then
    assertThat(actual)
      .usingRecursiveComparison()
      .isEqualTo(expected);
  }

  @Test
  @DisplayName("이미 존재하는 활동을 추가하면 예외를 반환한다.")
  void findActivity_duplicate_fail() {
    //given
    final String activityName = "DDD";
    final ActivityAddRequest request = new ActivityAddRequest(ActivityType.CLUB, activityName);
    activityCommandService.addActivity(request);

    //when
    final ThrowingCallable actual = () -> activityCommandService.addActivity(request);

    //then
    assertThatThrownBy(actual)
      .isInstanceOf(ActivityException.class)
      .hasMessage(ActivityExceptionType.ALEADY_EXIST_ACTIVITY.errorMessage());
  }
}
