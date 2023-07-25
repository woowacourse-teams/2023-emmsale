package com.emmsale.event.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class EventServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private EventService eventService;

  @Nested
  @DisplayName("연도&월 별 필터링 및 정렬 기능 테스트")
  class dateFilterAndSort {

    @Test
    @DisplayName("2023년 7월 21일에 2023년 7월 행사를 조회하면, 해당 연도&월에 걸쳐있는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_2023_7() {
      // given
      final List<EventResponse> expectedEvents = List.of(
          new EventResponse(null, "인프콘 2023", null, null, List.of(), "진행 중"),
          new EventResponse(null, "웹 컨퍼런스", null, null, List.of(), "진행 중"),
          new EventResponse(null, "AI 컨퍼런스", null, null, List.of(), "진행 예정"),
          new EventResponse(null, "안드로이드 컨퍼런스", null, null, List.of(), "종료된 행사")
      );

      // when
      final List<EventResponse> actualEvents = eventService.findEvents(LocalDate.of(2023, 7, 21),
          2023, 7, null, null);

      // then
      assertThat(actualEvents).usingRecursiveComparison().comparingOnlyFields("name", "status")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("2023년 7월 21일에 2023년 8월 행사를 조회하면, 해당 연도&월에 걸쳐있는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_2023_8() {
      // given
      final List<EventResponse> expectedEvents = List.of(
          new EventResponse(null, "인프콘 2023", null, null, List.of(), "진행 중"),
          new EventResponse(null, "웹 컨퍼런스", null, null, List.of(), "진행 중"),
          new EventResponse(null, "모바일 컨퍼런스", null, null, List.of(), "진행 예정")
      );

      // when
      final List<EventResponse> actualEvents = eventService.findEvents(LocalDate.of(2023, 7, 21),
          2023, 8, null, null);

      // then
      assertThat(actualEvents).usingRecursiveComparison().comparingOnlyFields("name", "status")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("2023년 7월 21일에 2023년 6월 행사를 조회하면, 해당 연도&월에 걸쳐있는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_2023_6() {
      // given
      final List<EventResponse> expectedEvents = List.of(
          new EventResponse(null, "인프콘 2023", null, null, List.of(), "진행 중"),
          new EventResponse(null, "안드로이드 컨퍼런스", null, null, List.of(), "종료된 행사")
      );

      // when
      final List<EventResponse> actualEvents = eventService.findEvents(LocalDate.of(2023, 7, 21),
          2023, 6, null, null);

      // then
      assertThat(actualEvents).usingRecursiveComparison().comparingOnlyFields("name", "status")
          .isEqualTo(expectedEvents);
    }
  }
}
