package com.emmsale.career.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.emmsale.career.application.dto.ActivityResponse;
import com.emmsale.career.application.dto.CareerResponse;
import com.emmsale.career.domain.ActivityType;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CareerServiceTest {

  @Autowired
  private CareerService careerService;

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

    final List<String> expectedCareerNames = List.of(
        "YAPP", "DND",
        "nexters", "인프콘",
        "우아한테크코스", "Backend"
    );

    //when
    List<CareerResponse> careerResponses = careerService.findAll();

    //then
    final List<String> actualCareerNames = careerResponses.stream()
        .flatMap(it -> it.getActivityResponses().stream())
        .map(ActivityResponse::getName)
        .collect(Collectors.toList());

    assertAll(
        () -> assertThat(careerResponses).hasSize(4),
        () -> assertThat(careerResponses)
            .extracting("activityName")
            .containsExactlyInAnyOrderElementsOf(expectedActivities),
        () -> assertThat(expectedCareerNames)
            .containsExactlyInAnyOrderElementsOf(actualCareerNames)
    );
  }


}
