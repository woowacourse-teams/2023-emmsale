package com.emmsale.event.application;

import static com.emmsale.event.EventFixture.AI_아이디어_공모전;
import static com.emmsale.event.EventFixture.AI_컨퍼런스;
import static com.emmsale.event.EventFixture.createEventParticipateRequest;
import static com.emmsale.event.EventFixture.eventFixture;
import static com.emmsale.event.EventFixture.구름톤;
import static com.emmsale.event.EventFixture.날짜_8월_10일;
import static com.emmsale.event.EventFixture.모바일_컨퍼런스;
import static com.emmsale.event.EventFixture.안드로이드_컨퍼런스;
import static com.emmsale.event.EventFixture.웹_컨퍼런스;
import static com.emmsale.event.EventFixture.인프콘_2023;
import static com.emmsale.event.exception.EventExceptionType.ALREADY_PARTICIPATED;
import static com.emmsale.event.exception.EventExceptionType.FORBIDDEN_PARTICIPATE_EVENT;
import static com.emmsale.event.exception.EventExceptionType.INVALID_MONTH;
import static com.emmsale.event.exception.EventExceptionType.INVALID_STATUS;
import static com.emmsale.event.exception.EventExceptionType.INVALID_YEAR;
import static com.emmsale.event.exception.EventExceptionType.INVALID_YEAR_AND_MONTH;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_PARTICIPANT;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_TAG;
import static com.emmsale.event.exception.EventExceptionType.START_DATE_TIME_AFTER_END_DATE_TIME;
import static com.emmsale.member.MemberFixture.memberFixture;
import static com.emmsale.tag.TagFixture.AI;
import static com.emmsale.tag.TagFixture.IOS;
import static com.emmsale.tag.TagFixture.백엔드;
import static com.emmsale.tag.TagFixture.안드로이드;
import static com.emmsale.tag.TagFixture.프론트엔드;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.EventParticipateRequest;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.application.dto.ParticipantResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventTag;
import com.emmsale.event.domain.EventType;
import com.emmsale.event.domain.Participant;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.EventTagRepository;
import com.emmsale.event.domain.repository.ParticipantRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.tag.application.dto.TagRequest;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import com.emmsale.tag.exception.TagException;
import com.emmsale.tag.exception.TagExceptionType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class EventServiceTest extends ServiceIntegrationTestHelper {

  private static final EventResponse 인프콘_2023 = new EventResponse(null, "인프콘 2023", null, null,
      List.of(), "진행 중", null, 0);
  private static final EventResponse 웹_컨퍼런스 = new EventResponse(null, "웹 컨퍼런스", null, null,
      List.of(), "진행 중", null, 0);
  private static final EventResponse 안드로이드_컨퍼런스 = new EventResponse(null, "안드로이드 컨퍼런스", null, null,
      List.of(), "종료된 행사", null, 0);
  private static final EventResponse AI_컨퍼런스 = new EventResponse(null, "AI 컨퍼런스", null, null,
      List.of(), "진행 예정", null, 0);
  private static final EventResponse 모바일_컨퍼런스 = new EventResponse(null, "모바일 컨퍼런스", null, null,
      List.of(), "진행 예정", null, 0);
  private static final EventResponse AI_아이디어_공모전 = new EventResponse(null, "AI 아이디어 공모전", null,
      null,
      List.of(), "종료된 행사", null, 0);
  private static final EventResponse 구름톤 = new EventResponse(null, "구름톤", null, null,
      List.of(), "진행 중", null, 0);


  private static final LocalDate TODAY = LocalDate.of(2023, 7, 21);
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private EventService eventService;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private EventTagRepository eventTagRepository;
  @Autowired
  private ParticipantRepository participantRepository;
  @Autowired
  private TagRepository tagRepository;

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
  }

  @Test
  @DisplayName("event의 id로 참가 게시글 목록을 조회할 수 있다.")
  void findParticipants() {
    // given
    final Event 인프콘 = eventRepository.save(eventFixture());
    final Member 멤버1 = memberRepository.save(new Member(123L, "image1.com"));
    final Member 멤버2 = memberRepository.save(new Member(124L, "image2.com"));

    final EventParticipateRequest requestMember1 = createEventParticipateRequest(멤버1);
    final EventParticipateRequest requestMember2 = createEventParticipateRequest(멤버2);

    final Long 멤버1_참가글_ID = eventService.participate(인프콘.getId(), requestMember1, 멤버1);
    final Long 멤버2_참가글_ID = eventService.participate(인프콘.getId(), requestMember2, 멤버2);

    final List<ParticipantResponse> expected = List.of(
        new ParticipantResponse(멤버1_참가글_ID, 멤버1.getId(), 멤버1.getName(), 멤버1.getImageUrl(),
            멤버1.getDescription(), requestMember1.getContent(), LocalDate.now(), LocalDate.now()),
        new ParticipantResponse(멤버2_참가글_ID, 멤버2.getId(), 멤버2.getName(), 멤버2.getImageUrl(),
            멤버2.getDescription(), requestMember2.getContent(), LocalDate.now(), LocalDate.now())
    );

    //when
    final List<ParticipantResponse> actual = eventService.findParticipants(인프콘.getId());

    //then
    assertThat(actual)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  @Nested
  @DisplayName("id로 이벤트를 조회할 수 있다.")
  class findEventTest {

    @Test
    @DisplayName("event의 id로 해당하는 event를 조회할 수 있다.")
    void success() {
      //given
      final Event event = eventRepository.save(eventFixture());
      final EventDetailResponse expected = EventDetailResponse.from(event, 날짜_8월_10일());

      //when
      final EventDetailResponse actual = eventService.findEvent(event.getId(), 날짜_8월_10일());

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

      //when, then
      assertThatThrownBy(() -> eventService.findEvent(notFoundEventId, 날짜_8월_10일()))
          .isInstanceOf(EventException.class)
          .hasMessage(NOT_FOUND_EVENT.errorMessage());
    }
  }

  @Nested
  @DisplayName("event 참가자 목록에 멤버를 추가할 수 있다.")
  class Participate {

    @Test
    @DisplayName("정상적으로 멤버를 추가한다.")
    void success() {
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();
      final Event 인프콘 = eventRepository.save(eventFixture());

      final Long participantId = eventService.participate(인프콘.getId(),
          createEventParticipateRequest(member), member);

      assertThat(participantId)
          .isNotNull();
    }

    @Test
    @DisplayName("memberId와 Member가 다르면 Exception이 발생한다.")
    void fail_forbidden() {
      final Long memberId = 1L;
      final Long otherMemberId = 2L;
      final Member member = memberRepository.findById(memberId).get();
      final EventParticipateRequest request = new EventParticipateRequest(otherMemberId, "빈 게시글");
      final Event 인프콘 = eventRepository.save(eventFixture());

      assertThatThrownBy(() -> eventService.participate(인프콘.getId(), request, member))
          .isInstanceOf(EventException.class)
          .hasMessage(FORBIDDEN_PARTICIPATE_EVENT.errorMessage());
    }

    @Test
    @DisplayName("이미 참가한 멤버면 Exception이 발생한다.")
    void fail_alreadyParticipate() {
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();
      final Event 인프콘 = eventRepository.save(eventFixture());
      final EventParticipateRequest request = createEventParticipateRequest(member);
      eventService.participate(인프콘.getId(), request, member);

      assertThatThrownBy(() -> eventService.participate(인프콘.getId(), request, member))
          .isInstanceOf(EventException.class)
          .hasMessage(ALREADY_PARTICIPATED.errorMessage());
    }
  }

  @Nested
  @DisplayName("event 참가자 목록에서 멤버를 삭제할 수 있다.")
  class CancelParticipate {

    @Test
    @DisplayName("정상적으로 멤버를 삭제한다.")
    void cancelParticipate_success() {
      //given
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();
      final Event 인프콘 = eventRepository.save(eventFixture());

      final Long participantId = eventService.participate(인프콘.getId(),
          createEventParticipateRequest(member), member);

      // when
      eventService.cancelParticipate(인프콘.getId(), memberId, member);
      final Optional<Participant> actual = participantRepository.findById(participantId);

      // then
      assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 행사면 예외가 발생한다.")
    void cancelParticipate_not_found_event() {
      // given
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();

      final Long invalidEventId = 999L;

      // when
      final ThrowingCallable actual = () -> eventService.cancelParticipate(invalidEventId, memberId,
          member);

      // then
      assertThatThrownBy(actual).isInstanceOf(EventException.class)
          .hasMessage(NOT_FOUND_EVENT.errorMessage());
    }

    @Test
    @DisplayName("존재하지 않는 참여자면 예외가 발생한다.")
    void cancelParticipate_not_found_participant() {
      // given
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();

      final Long eventId = eventRepository.save(eventFixture()).getId();

      // when
      final ThrowingCallable actual = () -> eventService.cancelParticipate(eventId, memberId,
          member);

      // then
      assertThatThrownBy(actual).isInstanceOf(EventException.class)
          .hasMessage(NOT_FOUND_PARTICIPANT.errorMessage());
    }
  }

  @Nested
  @DisplayName("findEvents() : 행사 목록 조회")
  class findEvents {

    @Test
    @DisplayName("2023년 7월 21일에 컨퍼런스 행사를 조회하면, 해당 카테고리에 해당하는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_CONFERENCE() {
      // given
      final List<EventResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스, AI_컨퍼런스, 모바일_컨퍼런스,
          안드로이드_컨퍼런스);

      // when
      final List<EventResponse> actualEvents = eventService.findEvents(EventType.CONFERENCE, TODAY,
          null, null, null, null);

      // then
      assertThat(actualEvents).usingRecursiveComparison().comparingOnlyFields("name", "status")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("2023년 7월 21일에 대회 행사를 조회하면, 해당 카테고리에 해당하는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_COMPETITION() {
      // given
      final List<EventResponse> expectedEvents = List.of(구름톤, AI_아이디어_공모전);

      // when
      final List<EventResponse> actualEvents = eventService.findEvents(EventType.COMPETITION, TODAY,
          null, null, null, null);

      // then
      assertThat(actualEvents).usingRecursiveComparison().comparingOnlyFields("name", "status")
          .isEqualTo(expectedEvents);
    }

    @Test
    @DisplayName("2023년 7월 21일에 2023년 7월 행사를 조회하면, 해당 연도&월에 걸쳐있는 모든 행사 목록을 조회할 수 있다.")
    void findEvents_2023_7() {
      // given
      final List<EventResponse> expectedEvents = List.of(인프콘_2023, 웹_컨퍼런스, AI_컨퍼런스, 안드로이드_컨퍼런스);

      // when
      final List<EventResponse> actualEvents = eventService.findEvents(EventType.CONFERENCE, TODAY,
          2023, 7, null, null);

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
      final List<EventResponse> actualEvents = eventService.findEvents(EventType.CONFERENCE, TODAY,
          2023, 8, null, null);

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
      final List<EventResponse> actualEvents = eventService.findEvents(EventType.CONFERENCE, TODAY,
          2023, 6, null, null);

      // then
      assertThat(actualEvents).usingRecursiveComparison().comparingOnlyFields("name", "status")
          .isEqualTo(expectedEvents);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "진행 중")
    @DisplayName("등록된 행사가 없을 경우, status 옵션이 있든 없든 빈 목록을 반환한다.")
    void findEvents_empty(final String statusName) {
      // given
      eventRepository.deleteAll();

      // when
      final List<EventResponse> actualEvents = eventService.findEvents(EventType.CONFERENCE, TODAY,
          2023, 12, null,
          statusName);

      // then
      assertThat(actualEvents).isEmpty();
    }

    @Test
    @DisplayName("아무 행사도 없는 2023년 12월 행사를 조회하면, 빈 목록을 반환한다.")
    void findEvents_2023_12() {
      // given, when
      final List<EventResponse> actualEvents = eventService.findEvents(EventType.CONFERENCE, TODAY,
          2023, 12, null, null);

      // then
      assertThat(actualEvents).isEmpty();
    }


    @Test
    @DisplayName("아무 행사도 없는 2023년 12월의 행사를 tag로 필터링하면, 빈 목록을 반환한다.")
    void findEvents_empty_tag_filter() {
      // given, when
      final List<EventResponse> actualEvents = eventService.findEvents(EventType.CONFERENCE, TODAY,
          2023, 12, "안드로이드",
          null);

      // then
      assertThat(actualEvents).isEmpty();
    }

    @Test
    @DisplayName("아무 행사도 없는 2023년 12월의 행사를 status로 필터링하면, 빈 목록을 반환한다.")
    void findEvents_empty_status_filter() {
      // given, when
      final List<EventResponse> actualEvents = eventService.findEvents(EventType.CONFERENCE, TODAY,
          2023, 12, null,
          "진행 중");

      // then
      assertThat(actualEvents).isEmpty();
    }

    @ParameterizedTest
    @CsvSource(value = {":3", "2023:"}, delimiter = ':')
    @DisplayName("연도와 월 중 하나의 값만 유효하게 들어오면 예외를 반환한다.")
    void findEvents_month_year_fail(final Integer year, final Integer month) {
      // given, when
      final ThrowingCallable actual = () -> eventService.findEvents(EventType.CONFERENCE, TODAY,
          year, month, null, null);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(EventException.class)
          .hasMessage(INVALID_YEAR_AND_MONTH.errorMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {2014, 0, -1})
    @DisplayName("유효하지 않은 값이 연도 값으로 들어오면 예외를 반환한다.")
    void findEvents_year_fail(final int year) {
      // given, when
      final ThrowingCallable actual = () -> eventService.findEvents(EventType.CONFERENCE, TODAY,
          year, 7, null, null);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(EventException.class)
          .hasMessage(INVALID_YEAR.errorMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 13, 14})
    @DisplayName("유효하지 않은 값이 월 값으로 들어오면 예외를 반환한다.")
    void findEvents_month_fail(final int month) {
      // given, when
      final ThrowingCallable actual = () -> eventService.findEvents(EventType.CONFERENCE, TODAY,
          2023, month, null, null);

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(EventException.class)
          .hasMessage(INVALID_MONTH.errorMessage());
    }

    @Test
    @DisplayName("'안드로이드' 태그를 포함하는 행사 목록을 조회할 수 있다.")
    void findEvents_tag_filter() {
      // given
      final List<EventResponse> expectedEvents = List.of(인프콘_2023, 안드로이드_컨퍼런스);

      // when
      final List<EventResponse> actualEvents = eventService.findEvents(EventType.CONFERENCE, TODAY,
          2023, 7, "안드로이드",
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
      final ThrowingCallable actual = () -> eventService.findEvents(EventType.CONFERENCE, TODAY,
          2023, 7, "개발", null);

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
      final List<EventResponse> actualEvents = eventService.findEvents(EventType.CONFERENCE, TODAY,
          2023, 7, null,
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
      final ThrowingCallable actual = () -> eventService.findEvents(EventType.CONFERENCE, TODAY,
          2023, 7, null,
          "존재하지 않는 상태");

      // then
      assertThatThrownBy(actual)
          .isInstanceOf(EventException.class)
          .hasMessage(INVALID_STATUS.errorMessage());
    }
  }

  @Nested
  class AddEvent {

    final List<TagRequest> tagRequests = List.of(
        new TagRequest(IOS().getName()),
        new TagRequest(AI().getName())
    );
    private final LocalDateTime beforeDateTime = LocalDateTime.now();
    private final LocalDateTime afterDateTime = beforeDateTime.plusDays(1);
    private final String eventName = "새로운 이름";
    private final String eventLocation = "새로운 장소";
    private final String eventInformationUrl = "https://새로운-상세-URL.com";
    private final String imageUrl = "https://image.com";
    private final EventType type = EventType.CONFERENCE;
    private final LocalDate now = LocalDate.now();

    @Test
    @DisplayName("이벤트를 성공적으로 저장한다.")
    void addEventTest() {
      //given
      final EventDetailRequest request = new EventDetailRequest(
          eventName,
          eventLocation,
          eventInformationUrl,
          beforeDateTime,
          afterDateTime,
          tagRequests,
          imageUrl,
          type
      );

      //when
      final EventDetailResponse response = eventService.addEvent(request, now);
      final Event savedEvent = eventRepository.findById(response.getId()).get();

      //then
      assertAll(
          () -> assertEquals(eventName, savedEvent.getName()),
          () -> assertEquals(eventLocation, savedEvent.getLocation()),
          () -> assertEquals(eventInformationUrl, savedEvent.getInformationUrl()),
          () -> assertEquals(beforeDateTime, savedEvent.getStartDate()),
          () -> assertEquals(afterDateTime, savedEvent.getEndDate()),
          () -> assertThat(savedEvent.getTags()).extracting("tag", Tag.class)
              .extracting("name", String.class)
              .containsAll(
                  tagRequests.stream()
                      .map(TagRequest::getName)
                      .collect(Collectors.toList())
              )
      );
    }

    @Test
    @DisplayName("행사 시작 일시가 행사 종료 일시 이후일 경우 EventException이 발생한다.")
    void addEventWithStartDateTimeAfterBeforeDateTimeTest() {
      //given
      final LocalDateTime startDateTime = afterDateTime;
      final LocalDateTime endDatetime = beforeDateTime;

      final EventDetailRequest request = new EventDetailRequest(
          eventName,
          eventLocation,
          eventInformationUrl,
          startDateTime,
          endDatetime,
          tagRequests,
          imageUrl,
          type
      );

      //when & then
      final EventException exception = assertThrowsExactly(EventException.class,
          () -> eventService.addEvent(request, now));

      assertEquals(exception.exceptionType(),
          START_DATE_TIME_AFTER_END_DATE_TIME);
    }

    @Test
    @DisplayName("Tag가 존재하지 않을 경우 EventException이 발생한다.")
    void addEventWithNotExistTagTest() {
      //given
      final List<TagRequest> tagRequests = List.of(
          new TagRequest(백엔드().getName()),
          new TagRequest(안드로이드().getName()),
          new TagRequest("존재하지 않는 태그")
      );

      final EventDetailRequest request = new EventDetailRequest(
          eventName,
          eventLocation,
          eventInformationUrl,
          beforeDateTime,
          afterDateTime,
          tagRequests,
          imageUrl,
          type
      );

      //when & then
      final EventException exception = assertThrowsExactly(EventException.class,
          () -> eventService.addEvent(request, now));

      assertEquals(exception.exceptionType(),
          NOT_FOUND_TAG);
    }
  }

  @Nested
  class UpdateEvent {

    final List<TagRequest> newTagRequests = List.of(
        new TagRequest(IOS().getName()),
        new TagRequest(AI().getName())
    );
    private final LocalDateTime beforeDateTime = LocalDateTime.now();
    private final LocalDateTime afterDateTime = beforeDateTime.plusDays(1);
    private final String newName = "새로운 이름";
    private final String newLocation = "새로운 장소";
    private final String newInformationUrl = "https://새로운-상세-URL.com";
    private final String imageUrl = "https://image.com";
    private final LocalDate now = LocalDate.now();

    @Test
    @DisplayName("이벤트를 성공적으로 업데이트한다.")
    void updateEventTest() {
      //given
      final LocalDateTime newStartDateTime = beforeDateTime;
      final LocalDateTime newEndDateTime = afterDateTime;

      final EventDetailRequest updateRequest = new EventDetailRequest(
          newName,
          newLocation,
          newInformationUrl,
          beforeDateTime,
          afterDateTime,
          newTagRequests,
          imageUrl,
          EventType.CONFERENCE
      );

      final Event event = eventRepository.save(인프콘_2023());
      final Long eventId = event.getId();

      //when
      eventService.updateEvent(eventId, updateRequest, now);
      final Event updatedEvent = eventRepository.findById(eventId).get();

      //then
      assertAll(
          () -> assertEquals(newName, updatedEvent.getName()),
          () -> assertEquals(newLocation, updatedEvent.getLocation()),
          () -> assertEquals(newStartDateTime, updatedEvent.getStartDate()),
          () -> assertEquals(newEndDateTime, updatedEvent.getEndDate()),
          () -> assertEquals(newInformationUrl, updatedEvent.getInformationUrl()),
          () -> assertThat(updatedEvent.getTags())
              .extracting("tag", Tag.class)
              .extracting("name", String.class)
              .containsAll(
                  newTagRequests.stream()
                      .map(TagRequest::getName)
                      .collect(Collectors.toList())
              )
      );
    }

    @Test
    @DisplayName("업데이트할 이벤트가 존재하지 않을 경우 EventException이 발생한다.")
    void updateEventWithNotExistsEventTest() {
      //given
      final long notExistsEventId = 0L;

      final EventDetailRequest updateRequest = new EventDetailRequest(
          newName,
          newLocation,
          newInformationUrl,
          beforeDateTime,
          afterDateTime,
          newTagRequests,
          imageUrl,
          EventType.CONFERENCE
      );

      //when & then
      final EventException exception = assertThrowsExactly(EventException.class,
          () -> eventService.updateEvent(notExistsEventId, updateRequest, now));

      assertEquals(exception.exceptionType(), NOT_FOUND_EVENT);
    }

    @Test
    @DisplayName("행사 시작 일시가 행사 종료 일시 이후일 경우 EventException이 발생한다.")
    void updateEventWithStartDateTimeAfterBeforeDateTimeTest() {
      //given
      final LocalDateTime newStartDateTime = afterDateTime;
      final LocalDateTime newEndDateTime = beforeDateTime;

      final EventDetailRequest updateRequest = new EventDetailRequest(
          newName,
          newLocation,
          newInformationUrl,
          newStartDateTime,
          newEndDateTime,
          newTagRequests,
          imageUrl,
          EventType.CONFERENCE
      );

      final Event event = eventRepository.save(인프콘_2023());
      final Long eventId = event.getId();

      //when & then
      final EventException exception = assertThrowsExactly(EventException.class,
          () -> eventService.updateEvent(eventId, updateRequest, now));

      assertEquals(exception.exceptionType(), START_DATE_TIME_AFTER_END_DATE_TIME);
    }

    @Test
    @DisplayName("Tag가 존재하지 않을 경우 EventException이 발생한다.")
    void updateEventWithNotExistTagTest() {
      //given
      final List<TagRequest> newTagRequests = List.of(
          new TagRequest("존재하지 않는 태그")
      );

      final EventDetailRequest updateRequest = new EventDetailRequest(
          newName,
          newLocation,
          newInformationUrl,
          beforeDateTime,
          afterDateTime,
          newTagRequests,
          imageUrl,
          EventType.CONFERENCE
      );

      final Event event = eventRepository.save(인프콘_2023());
      final Long eventId = event.getId();

      //when & then
      final EventException exception = assertThrowsExactly(EventException.class,
          () -> eventService.updateEvent(eventId, updateRequest, now));

      assertEquals(exception.exceptionType(), NOT_FOUND_TAG);
    }
  }

  @Nested
  class DeleteEvent {

    @Test
    @DisplayName("이벤트를 성공적으로 삭제한다.")
    void deleteEventTest() {
      //given
      final Event event = eventRepository.save(인프콘_2023());
      final Long eventId = event.getId();

      //when
      eventService.deleteEvent(eventId);

      //then
      assertFalse(eventRepository.findById(eventId).isPresent());
    }

    @Test
    @DisplayName("삭제할 이벤트가 존재하지 않을 경우 EventException이 발생한다.")
    void deleteEventWithNotExistsEventTest() {
      //given
      final long notExistsEventId = 0L;

      //when & then
      final EventException exception = assertThrowsExactly(EventException.class,
          () -> eventService.deleteEvent(notExistsEventId));

      assertEquals(exception.exceptionType(), NOT_FOUND_EVENT);
    }
  }

  @Nested
  @DisplayName("이벤트에 이미 참가한 멤버인지 확인할 수 있다.")
  class isAlreadyParticipate {

    @Test
    @DisplayName("이벤트에 이미 참가한 경우 true를 반환한다.")
    void alreadyParticipateThenTrue() {
      //given
      final Event 인프콘 = eventRepository.save(인프콘_2023());
      final Member 멤버 = memberRepository.save(memberFixture());
      eventService.participate(인프콘.getId(), createEventParticipateRequest(멤버), 멤버);

      //when
      final Boolean actual = eventService.isAlreadyParticipate(인프콘.getId(), 멤버.getId());

      //then
      assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("이벤트에 참가히자 않은 경우 false를 반환한다.")
    void isNotAlreadyParticipateThenFalse() {
      //given
      final Event 인프콘 = eventRepository.save(인프콘_2023());
      final Member 멤버 = memberRepository.save(memberFixture());

      //when
      final Boolean actual = eventService.isAlreadyParticipate(인프콘.getId(), 멤버.getId());

      //then
      assertThat(actual).isFalse();
    }
  }
}

