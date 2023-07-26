package com.emmsale.event.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.tag.exception.TagException;
import com.emmsale.tag.exception.TagExceptionType;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class EventServiceTest extends ServiceIntegrationTestHelper {

  public static final EventResponse 인프콘_2023 = new EventResponse(null, "인프콘 2023", null, null,
      List.of(), "진행 중");
  public static final EventResponse 웹_컨퍼런스 = new EventResponse(null, "웹 컨퍼런스", null, null,
      List.of(),
      "진행 중");
  public static final EventResponse 안드로이드_컨퍼런스 = new EventResponse(null, "안드로이드 컨퍼런스", null, null,
      List.of(), "종료된 행사");
  public static final EventResponse AI_컨퍼런스 = new EventResponse(null, "AI 컨퍼런스", null, null,
      List.of(), "진행 예정");
  public static final EventResponse 모바일_컨퍼런스 = new EventResponse(null, "모바일 컨퍼런스", null, null,
      List.of(), "진행 예정");
  private static final LocalDate TODAY = LocalDate.of(2023, 7, 21);
  @Autowired
  private EventService eventService;

  @Nested
  @DisplayName("findEvents() : 행사 목록 조회")
  class findEvents {

    @Test
    @DisplayName("2023년 7월 21일에 2023년 7월 행사를 조회하면, 해당 연도&월에 걸쳐있는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_2023_7() {
      // given
      final List<EventResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스, AI_컨퍼런스, 안드로이드_컨퍼런스);

      // when
      final List<EventResponse> actualEvents = eventService.findEvents(TODAY, 2023, 7, null, null);

      // then
      assertThat(actualEvents).usingRecursiveComparison().comparingOnlyFields("name", "status")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("2023년 7월 21일에 2023년 8월 행사를 조회하면, 해당 연도&월에 걸쳐있는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_2023_8() {
      // given
      final List<EventResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스, 모바일_컨퍼런스);

      // when
      final List<EventResponse> actualEvents = eventService.findEvents(TODAY, 2023, 8, null, null);

      // then
      assertThat(actualEvents).usingRecursiveComparison().comparingOnlyFields("name", "status")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("2023년 7월 21일에 2023년 6월 행사를 조회하면, 해당 연도&월에 걸쳐있는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_2023_6() {
      // given
      final List<EventResponse> expectedEvents = List.of(인프콘_2023, 안드로이드_컨퍼런스);

      // when
      final List<EventResponse> actualEvents = eventService.findEvents(TODAY, 2023, 6, null, null);

      // then
      assertThat(actualEvents).usingRecursiveComparison().comparingOnlyFields("name", "status")
          .isEqualTo(expectedEvents);
    }

    @ParameterizedTest
    @ValueSource(ints = {2014, 0, -1})
    @DisplayName("유효하지 않은 값이 연도 값으로 들어오면 예외를 반환한다.")
    void findEvents_year_fail(int year) {
      // given, when
      final ThrowingCallable actual = () -> eventService.findEvents(TODAY, year, 7, null, null);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(EventException.class)
          .hasMessage(EventExceptionType.INVALID_YEAR.errorMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 13, 14})
    @DisplayName("유효하지 않은 값이 월 값으로 들어오면 예외를 반환한다.")
    void findEvents_month_fail(int month) {
      // given, when
      final ThrowingCallable actual = () -> eventService.findEvents(TODAY, 2023, month, null, null);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(EventException.class)
          .hasMessage(EventExceptionType.INVALID_MONTH.errorMessage());
    }

    @Test
    @DisplayName("'안드로이드' 태그를 포함하는 행사 목록을 조회할 수 있다.")
    void findEvents_tag_filter() {
      // given
      final List<EventResponse> expectedEvents = List.of(인프콘_2023, 안드로이드_컨퍼런스);

      // when
      final List<EventResponse> actualEvents = eventService.findEvents(TODAY, 2023, 7, "안드로이드",
          null);

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("존재하지 않는 태그가 입력으로 들어오면 예외를 반환한다.")
    void findEvents_tag_filter_fail() {
      // given, when
      final ThrowingCallable actual = () -> eventService.findEvents(TODAY, 2023, 7, "개발", null);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(TagException.class)
          .hasMessage(TagExceptionType.NOT_FOUND_TAG.errorMessage());
    }

    @Test
    @DisplayName("'진행 중' 상태의 행사 목록을 조회할 수 있다.")
    void findEvents_status_filter() {
      // given
      final List<EventResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스);

      // when
      final List<EventResponse> actualEvents = eventService.findEvents(TODAY, 2023, 7, null,
          "진행 중");

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("잘못된 양식의 status 가 입력으로 들어오면 예외를 반환한다.")
    void findEvents_status_filter_fail() {
      // given, when
      final ThrowingCallable actual = () -> eventService.findEvents(TODAY, 2023, 7, null,
          "존재하지 않는 상태");

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(EventException.class)
          .hasMessage(EventExceptionType.INVALID_STATUS.errorMessage());
    }
  }
}
