package com.emmsale.activity.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.activity.application.dto.ActivityResponse;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ActivityQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private ActivityQueryService activityQueryService;

  @Test
  @DisplayName("존재하고 있는 Activity를 전체 조회할 수 있다.")
  void findAll() {
    //given
    final List<ActivityResponse> expected = List.of(
      new ActivityResponse(1L, "동아리", "YAPP"),
      new ActivityResponse(2L, "동아리", "DND"),
      new ActivityResponse(3L, "동아리", "nexters"),
      new ActivityResponse(4L, "컨퍼런스", "인프콘"),
      new ActivityResponse(5L, "교육", "우아한테크코스"),
      new ActivityResponse(6L, "직무", "Backend")
    );

    //when
    final List<ActivityResponse> actual = activityQueryService.findAll();

    //then
    assertThat(actual)
      .usingRecursiveComparison()
      .isEqualTo(expected);
  }
}
