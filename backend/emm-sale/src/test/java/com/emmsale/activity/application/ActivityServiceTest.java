package com.emmsale.activity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.emmsale.activity.application.dto.ActivityResponse;
import com.emmsale.activity.application.dto.ActivityResponses;
import com.emmsale.activity.domain.ActivityType;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ActivityServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private ActivityService activityService;

  @Test
  @DisplayName("존재하고 있는 커리어를 전체 조회할 수 있다.")
  void findAll() throws Exception {
    //given
    final List<String> expectedActivities = List.of(
        ActivityType.CLUB.getValue(),
        ActivityType.CONFERENCE.getValue(),
        ActivityType.JOB.getValue(),
        ActivityType.EDUCATION.getValue()
    );

    final List<String> expectedActivityNames = List.of(
        "YAPP", "DND",
        "nexters", "인프콘",
        "우아한테크코스", "Backend"
    );

    //when
    List<ActivityResponses> activityResponses = activityService.findAll();

    //then
    final List<String> actualActivityNames = activityResponses.stream()
        .flatMap(it -> it.getActivityResponses().stream())
        .map(ActivityResponse::getName)
        .collect(Collectors.toList());

    assertAll(
        () -> assertThat(activityResponses).hasSize(4),
        () -> assertThat(activityResponses)
            .extracting("activityType")
            .containsExactlyInAnyOrderElementsOf(expectedActivities),
        () -> assertThat(expectedActivityNames)
            .containsExactlyInAnyOrderElementsOf(actualActivityNames)
    );
  }
}
