package com.emmsale.event.application;

import static com.emmsale.event.EventFixture.AI_아이디어_공모전;
import static com.emmsale.event.EventFixture.AI_컨퍼런스;
import static com.emmsale.event.EventFixture.eventFixture;
import static com.emmsale.event.EventFixture.구름톤;
import static com.emmsale.event.EventFixture.날짜_8월_10일;
import static com.emmsale.event.EventFixture.모바일_컨퍼런스;
import static com.emmsale.event.EventFixture.안드로이드_컨퍼런스;
import static com.emmsale.event.EventFixture.웹_컨퍼런스;
import static com.emmsale.event.EventFixture.인프콘_2023;
import static com.emmsale.event.domain.EventStatus.IN_PROGRESS;
import static com.emmsale.event.exception.EventExceptionType.INVALID_DATE_FORMAT;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
import static com.emmsale.event.exception.EventExceptionType.START_DATE_AFTER_END_DATE;
import static com.emmsale.image.ImageFixture.행사_이미지1;
import static com.emmsale.image.ImageFixture.행사_이미지2;
import static com.emmsale.image.ImageFixture.행사_이미지3;
import static com.emmsale.tag.TagFixture.AI;
import static com.emmsale.tag.TagFixture.IOS;
import static com.emmsale.tag.TagFixture.백엔드;
import static com.emmsale.tag.TagFixture.안드로이드;
import static com.emmsale.tag.TagFixture.프론트엔드;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventMode;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventTag;
import com.emmsale.event.domain.EventType;
import com.emmsale.event.domain.PaymentType;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.EventTagRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.image.domain.Image;
import com.emmsale.image.domain.ImageType;
import com.emmsale.image.domain.repository.ImageRepository;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import com.emmsale.tag.exception.TagException;
import com.emmsale.tag.exception.TagExceptionType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class EventQueryServiceTest extends ServiceIntegrationTestHelper {

  private static final EventDetailResponse 인프콘_2023 = new EventDetailResponse(null, "인프콘 2023",
      null, null, null, null, null, "코엑스", List.of("백엔드"),
      "이미지1", EventType.CONFERENCE.name(), List.of(), "인프런", PaymentType.PAID.getValue(),
      EventMode.OFFLINE.getValue());
  private static final EventDetailResponse 웹_컨퍼런스 = new EventDetailResponse(null, "웹 컨퍼런스", null,
      null, null,
      null, null, "코엑스", List.of("백엔드"), "이미지1", EventType.CONFERENCE.name(),
      List.of(), "주최기관", PaymentType.PAID.getValue(), EventMode.ONLINE.getValue());
  private static final EventDetailResponse 안드로이드_컨퍼런스 = new EventDetailResponse(null, "안드로이드 컨퍼런스",
      null, null, null, null, null, "코엑스", List.of("백엔드"),
      "이미지1", EventType.CONFERENCE.name(), List.of(), "주최기관", PaymentType.PAID.getValue(),
      EventMode.ONLINE.getValue());
  private static final EventDetailResponse AI_컨퍼런스 = new EventDetailResponse(null, "AI 컨퍼런스",
      null, null, null, null, null, "코엑스", List.of("백엔드"),
      "이미지1", EventType.CONFERENCE.name(), List.of(), "주최기관", PaymentType.PAID.getValue(),
      EventMode.ONLINE.getValue());
  private static final EventDetailResponse 모바일_컨퍼런스 = new EventDetailResponse(null, "모바일 컨퍼런스",
      null, null, null, null, null, "코엑스", List.of("백엔드"),
      "이미지1", EventType.CONFERENCE.name(), List.of(), "주최기관", PaymentType.PAID.getValue(),
      EventMode.ONLINE.getValue());
  private static final EventDetailResponse AI_아이디어_공모전 = new EventDetailResponse(null,
      "AI 아이디어 공모전", null, null, null, null, null, "코엑스",
      List.of("백엔드"), "이미지1", EventType.CONFERENCE.name(), List.of(), "주최기관",
      PaymentType.PAID.getValue(), EventMode.ONLINE.getValue());
  private static final EventDetailResponse 구름톤 = new EventDetailResponse(null, "구름톤", null,
      null, null, null, null, "코엑스", List.of("백엔드"),
      "이미지1", EventType.COMPETITION.name(), List.of(), "주최기관", PaymentType.PAID.getValue(),
      EventMode.ONLINE.getValue());

  private static final LocalDateTime TODAY = LocalDateTime.of(2023, 7, 21, 0, 0);
  @Autowired
  private EventQueryService eventQueryService;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private EventTagRepository eventTagRepository;
  @Autowired
  private TagRepository tagRepository;
  @Autowired
  private ImageRepository imageRepository;

  @BeforeEach
  void init() {
    final Tag 백엔드 = tagRepository.save(백엔드());
    final Tag 프론트엔드 = tagRepository.save(프론트엔드());
    final Tag 안드로이드 = tagRepository.save(안드로이드());
    final Tag IOS = tagRepository.save(IOS());
    final Tag AI = tagRepository.save(AI());

    final Event 인프콘_2023 = eventRepository.save(인프콘_2023());
    final Event AI_컨퍼런스 = eventRepository.save(AI_컨퍼런스());
    final Event 모바일_컨퍼런스 = eventRepository.save(모바일_컨퍼런스());
    final Event 안드로이드_컨퍼런스 = eventRepository.save(안드로이드_컨퍼런스());
    final Event 웹_컨퍼런스 = eventRepository.save(웹_컨퍼런스());
    final Event AI_아이디어_공모전 = eventRepository.save(AI_아이디어_공모전());
    final Event 구름톤 = eventRepository.save(구름톤());

    eventTagRepository.saveAll(List.of(
        new EventTag(인프콘_2023, 백엔드), new EventTag(인프콘_2023, 프론트엔드), new EventTag(인프콘_2023, 안드로이드),
        new EventTag(인프콘_2023, IOS), new EventTag(인프콘_2023, AI), new EventTag(AI_컨퍼런스, AI),
        new EventTag(모바일_컨퍼런스, 안드로이드), new EventTag(모바일_컨퍼런스, IOS), new EventTag(안드로이드_컨퍼런스, 안드로이드),
        new EventTag(웹_컨퍼런스, 백엔드), new EventTag(웹_컨퍼런스, 프론트엔드))
    );
    imageRepository.saveAll(List.of(
        행사_이미지1(인프콘_2023.getId()),
        행사_이미지2(인프콘_2023.getId()),
        행사_이미지3(인프콘_2023.getId())
    ));
    imageRepository.saveAll(List.of(
        행사_이미지1(웹_컨퍼런스.getId()),
        행사_이미지2(웹_컨퍼런스.getId())
    ));
    imageRepository.saveAll(List.of(
        행사_이미지1(안드로이드_컨퍼런스.getId())
    ));
  }

  @Nested
  @DisplayName("id로 이벤트를 조회할 수 있다.")
  class findEventTest {

    @Test
    @DisplayName("event의 id로 해당하는 event를 조회할 수 있다.")
    void success() {
      //given
      final Event event = eventRepository.save(eventFixture());
      imageRepository.save(
          new Image("imageUrl1", ImageType.EVENT, event.getId(), 1, LocalDateTime.now())
      );
      imageRepository.save(
          new Image("imageUrl2", ImageType.EVENT, event.getId(), 0, LocalDateTime.now())
      );
      imageRepository.save(
          new Image("imageUrl3", ImageType.EVENT, event.getId(), 2, LocalDateTime.now())
      );

      final String thumbnailUrl = "imageUrl2";
      final List<String> imageUrls = List.of("imageUrl1", "imageUrl3");

      final EventDetailResponse expected = EventDetailResponse.from(event, thumbnailUrl,
          imageUrls);

      //when
      final EventDetailResponse actual = eventQueryService.findEvent(event.getId(), 날짜_8월_10일());

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expected);
    }

    @Test
    @DisplayName("이미지가 등록되지 않는 event의 imageUrl은 null, imageUrls는 빈 리스트로 반환된다.")
    void success_imageUrl_null_imageUrls_empty() {
      //given
      final Event event = eventRepository.save(eventFixture());

      final EventDetailResponse expected = EventDetailResponse.from(event, null,
          Collections.emptyList());

      //when
      final EventDetailResponse actual = eventQueryService.findEvent(event.getId(), 날짜_8월_10일());

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expected);
    }

    @Test
    @DisplayName("이미지가 하나만 등록된 event의 imageUrls은 빈 리스트로 반환된다.")
    void success_imageUrls_empty() {
      //given
      final Event event = eventRepository.save(eventFixture());
      imageRepository.save(
          new Image("imageUrl2", ImageType.EVENT, event.getId(), 0, LocalDateTime.now())
      );

      final String thumbnailUrl = "imageUrl2";
      final EventDetailResponse expected = EventDetailResponse.from(event, thumbnailUrl,
          Collections.emptyList());

      //when
      final EventDetailResponse actual = eventQueryService.findEvent(event.getId(), 날짜_8월_10일());

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expected);
    }

    @Test
    @DisplayName("요청한 id에 해당하는 event가 존재하지 않으면 Exception을 던진다.")
    void fail_EventNotFoundException() {
      //given
      final Long notFoundEventId = Long.MAX_VALUE;
      final LocalDate today = 날짜_8월_10일();
      //when, then
      assertThatThrownBy(() ->
          eventQueryService.findEvent(notFoundEventId, today))
          .isInstanceOf(EventException.class)
          .hasMessage(NOT_FOUND_EVENT.errorMessage());
    }
  }

  @Nested
  @DisplayName("findEvents() : 행사 목록 조회")
  class findEvents {

    @Test
    @DisplayName("2023년 7월 21일에 행사를 조회하면, 모든 행사 목록을 조회할 수 있다.")
    void findEvents_all() {
      // given
      final List<EventDetailResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스, 구름톤, AI_컨퍼런스,
          모바일_컨퍼런스,
          안드로이드_컨퍼런스, AI_아이디어_공모전);

      // when
      final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(null, TODAY,
          null, null, null, null, null);

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status", "applyStatus")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("2023년 7월 21일에 컨퍼런스 행사를 조회하면, 해당 카테고리에 해당하는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_CONFERENCE() {
      // given
      final List<EventDetailResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스, AI_컨퍼런스, 모바일_컨퍼런스,
          안드로이드_컨퍼런스);

      // when
      final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(
          EventType.CONFERENCE,
          TODAY,
          null, null, null, null, null);

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status", "applyStatus")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("2023년 7월 21일에 대회 행사를 조회하면, 해당 카테고리에 해당하는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_COMPETITION() {
      // given
      final List<EventDetailResponse> expectedEvents = List.of(구름톤, AI_아이디어_공모전);

      // when
      final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(
          EventType.COMPETITION,
          TODAY,
          null, null, null, null, null);

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status", "applyStatus")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("2023년 7월 21일에 2023년 7월 행사를 조회하면, 해당 기간에 걸쳐있는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_2023_7() {
      // given
      final List<EventDetailResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스, AI_컨퍼런스,
          안드로이드_컨퍼런스);

      // when
      final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(
          EventType.CONFERENCE,
          TODAY,
          "2023-07-01", "2023-07-31", null, null, null);

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status", "applyStatus")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("2023년 7월 21일에 2023년 8월 행사를 조회하면, 해당 기간에 걸쳐있는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_2023_8() {
      // given
      final List<EventDetailResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스, 모바일_컨퍼런스);

      // when
      final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(
          EventType.CONFERENCE,
          TODAY,
          "2023-08-01", "2023-08-31", null, null, null);

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status", "applyStatus")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("2023년 7월 21일에 2023년 6월 행사를 조회하면, 해당 기간에 걸쳐있는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_2023_6() {
      // given
      final List<EventDetailResponse> expectedEvents = List.of(인프콘_2023, 안드로이드_컨퍼런스);

      // when
      final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(
          EventType.CONFERENCE,
          TODAY,
          "2023-06-01", "2023-06-30", null, null, null);

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status", "applyStatus")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("2023년 7월 21일에 2023년 7월 17일 이후에 있는 행사를 조회하면, 해당 기간에 걸쳐있는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_after_2023_7_17() {
      // given
      final List<EventDetailResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스, AI_컨퍼런스, 모바일_컨퍼런스);

      // when
      final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(
          EventType.CONFERENCE,
          TODAY,
          "2023-07-17", null, null, null, null);

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status", "applyStatus")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("2023년 7월 21일에 2023년 7월 31일 이전에 있는 행사를 조회하면, 해당 기간에 걸쳐있는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_before_2023_7_31() {
      // given
      final List<EventDetailResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스, AI_컨퍼런스,
          안드로이드_컨퍼런스);

      // when
      final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(
          EventType.CONFERENCE,
          TODAY,
          null, "2023-07-31", null, null, null);

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status", "applyStatus")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("아무 행사도 없는 2023년 12월 행사를 조회하면, 빈 목록을 반환한다.")
    void findEvents_2023_12() {
      // given, when
      final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(
          EventType.CONFERENCE,
          TODAY,
          "2023-12-01", "2023-12-31", null, null, null);

      // then
      assertThat(actualEvents).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcde", "00-0-0", "-1-1-1-1", "2023-02-30"})
    @DisplayName("유효하지 않은 값이 시작일 정보로 들어오면 예외를 반환한다.")
    void findEvents_start_date_fail(final String startDate) {
      // given, when
      final ThrowingCallable actual = () -> eventQueryService.findEvents(EventType.CONFERENCE,
          TODAY,
          startDate, "2023-07-31", null, null, null);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(EventException.class)
          .hasMessage(INVALID_DATE_FORMAT.errorMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcde", "00-0-0", "-1-1-1-1", "2023-02-30"})
    @DisplayName("유효하지 않은 값이 종료일 값으로 들어오면 예외를 반환한다.")
    void findEvents_end_date_fail(final String endDate) {
      // given, when
      final ThrowingCallable actual = () -> eventQueryService.findEvents(EventType.CONFERENCE,
          TODAY,
          "2023-07-01", endDate, null, null, null);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(EventException.class)
          .hasMessage(INVALID_DATE_FORMAT.errorMessage());
    }

    @Test
    @DisplayName("시작일이 종료일보다 뒤에 있으면 예외를 반환한다.")
    void findEvents_start_after_end_fail() {
      // given, when
      final ThrowingCallable actual = () -> eventQueryService.findEvents(EventType.CONFERENCE,
          TODAY,
          "2023-07-16", "2023-07-15", null, null, null);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(EventException.class)
          .hasMessage(START_DATE_AFTER_END_DATE.errorMessage());
    }

    @Test
    @DisplayName("'안드로이드' 태그를 포함하는 행사 목록을 조회할 수 있다.")
    void findEvents_tag_filter() {
      // given
      final List<EventDetailResponse> expectedEvents = List.of(인프콘_2023, 모바일_컨퍼런스, 안드로이드_컨퍼런스);

      // when
      final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(
          EventType.CONFERENCE,
          TODAY,
          null, null, List.of("안드로이드"), null, null);

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status", "applyStatus")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("'안드로이드', '백엔드' 태그를 포함하는 행사 목록을 조회할 수 있다.")
    void findEvents_tags_filter() {
      // given
      final List<EventDetailResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스, 모바일_컨퍼런스,
          안드로이드_컨퍼런스);

      // when
      final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(
          EventType.CONFERENCE,
          TODAY,
          null, null, List.of("안드로이드", "백엔드"), null, null);

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status", "applyStatus")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("존재하지 않는 태그가 입력으로 들어오면 예외를 반환한다.")
    void findEvents_tag_filter_fail() {
      // given, when
      final ThrowingCallable actual = () -> eventQueryService.findEvents(EventType.CONFERENCE,
          TODAY,
          null, null, List.of("개발"), null, null);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(TagException.class)
          .hasMessage(TagExceptionType.NOT_FOUND_TAG.errorMessage());
    }

    @Test
    @DisplayName("'진행 중' 상태의 행사 목록을 조회할 수 있다.")
    void findEvents_status_filter() {
      // given
      final List<EventDetailResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스);

      // when
      final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(
          EventType.CONFERENCE,
          TODAY,
          null, null, null, List.of(IN_PROGRESS), null);

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status", "applyStatus")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("'진행 중' 및 '진행 예정' 상태의 행사 목록을 조회할 수 있다.")
    void findEvents_statuses_filter() {
      // given
      final List<EventDetailResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스, AI_컨퍼런스, 모바일_컨퍼런스);

      // when
      final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(
          EventType.CONFERENCE,
          TODAY,
          null, null, null, List.of(EventStatus.UPCOMING, IN_PROGRESS), null);

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status", "applyStatus")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("9월에 존재하는 진행 예정인 '안드로이드', '백엔드' 태그를 포함하는 행사 목록을 조회할 수 있다.")
    void findEvents_period_tags_filter() {
      // given
      final List<EventDetailResponse> expectedEvents = List.of(모바일_컨퍼런스);

      // when
      final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(
          EventType.CONFERENCE,
          TODAY,
          "2023-09-01", "2023-09-30", List.of("안드로이드", "백엔드"), List.of(EventStatus.UPCOMING), null);

      // then
      assertThat(actualEvents)
          .usingRecursiveComparison()
          .comparingOnlyFields("name", "status", "applyStatus")
          .isEqualTo(expectedEvents);
    }

    @Nested
    @DisplayName("특정 키워드로 행사를 검색할 수 있다.")
    class SearchEvent {

      @ParameterizedTest
      @ValueSource(strings = {"", " ", "      ", "\r", "\n", "\t"})
      @DisplayName("2023년 7월 21일에 컨퍼런스 행사를 조회할 때, 검색어가 공백인 경우 해당 카테고리에 해당하는 모든 행사 목록을 조회할 수 있다.")
      void findEvents_blank_search(final String keyword) {
        // given
        final List<EventDetailResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스, 구름톤, AI_컨퍼런스,
            모바일_컨퍼런스,
            안드로이드_컨퍼런스, AI_아이디어_공모전);

        // when
        final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(null,
            TODAY,
            null, null, null, null, keyword);

        // then
        assertThat(actualEvents)
            .usingRecursiveComparison()
            .comparingOnlyFields("name", "status", "applyStatus")
            .isEqualTo(expectedEvents);
      }

      @Test
      @DisplayName("2023년 7월 21일에 컨퍼런스 행사를 조회할 때, 검색 키워드가 들어오면 이름에 해당 검색 키워드를 포함하고 있는 행사 목록을 조회할 수 있다.")
      void findEvents_search() {
        // given
        final String keyword = " 컨퍼런스";
        final List<EventDetailResponse> expectedEvents = List.of(웹_컨퍼런스, AI_컨퍼런스, 모바일_컨퍼런스,
            안드로이드_컨퍼런스);

        // when
        final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(null,
            TODAY, null, null, null, null, keyword);

        // then
        assertThat(actualEvents)
            .usingRecursiveComparison()
            .comparingOnlyFields("name", "status", "applyStatus")
            .isEqualTo(expectedEvents);
      }

      @Test
      @DisplayName("2023년 7월 21일에 컨퍼런스 행사를 조회할 때, 검색 키워드가 여러 토큰으로 들어오면 해당 토큰을 이름에 모두 포함하고 있는 행사 목록을 조회할 수 있다.")
      void findEvents_multiple_tokens_search() {
        // given
        final String keyword = " 모 컨퍼";
        final List<EventDetailResponse> expectedEvents = List.of(모바일_컨퍼런스);

        // when
        final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(null,
            TODAY, null, null, null, null, keyword);

        // then
        assertThat(actualEvents)
            .usingRecursiveComparison()
            .comparingOnlyFields("name", "status", "applyStatus")
            .isEqualTo(expectedEvents);
      }

      @Test
      @DisplayName("'진행 중' 상태이고 특정 키워드를 포함하고 있는 행사 목록을 조회할 수 있다.")
      void findEvents_status_filter_and_search() {
        // given
        final String keyword = "프콘 ";
        final List<EventDetailResponse> expectedEvents = List.of(인프콘_2023);

        // when
        final List<EventDetailResponse> actualEvents = eventQueryService.findEvents(null,
            TODAY,
            null, null, null, List.of(IN_PROGRESS), keyword);

        // then
        assertThat(actualEvents)
            .usingRecursiveComparison()
            .comparingOnlyFields("name", "status", "applyStatus")
            .isEqualTo(expectedEvents);
      }
    }
  }
}
