package com.emmsale.event.application;

import static com.emmsale.event.EventFixture.AI_컨퍼런스;
import static com.emmsale.event.EventFixture.eventFixture;
import static com.emmsale.event.EventFixture.모바일_컨퍼런스;
import static com.emmsale.event.EventFixture.안드로이드_컨퍼런스;
import static com.emmsale.event.EventFixture.웹_컨퍼런스;
import static com.emmsale.event.EventFixture.인프콘_2023;
import static com.emmsale.tag.TagFixture.AI;
import static com.emmsale.tag.TagFixture.IOS;
import static com.emmsale.tag.TagFixture.백엔드;
import static com.emmsale.tag.TagFixture.안드로이드;
import static com.emmsale.tag.TagFixture.프론트엔드;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.application.dto.ParticipantResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventTag;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.EventTagRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import com.emmsale.tag.exception.TagException;
import com.emmsale.tag.exception.TagExceptionType;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class EventServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MemberRepository memberRepository;
  private static final EventResponse 인프콘_2023 = new EventResponse(null, "인프콘 2023", null, null,
      List.of(), "진행 중");
  private static final EventResponse 웹_컨퍼런스 = new EventResponse(null, "웹 컨퍼런스", null, null,
      List.of(),
      "진행 중");
  private static final EventResponse 안드로이드_컨퍼런스 = new EventResponse(null, "안드로이드 컨퍼런스", null, null,
      List.of(), "종료된 행사");
  private static final EventResponse AI_컨퍼런스 = new EventResponse(null, "AI 컨퍼런스", null, null,
      List.of(), "진행 예정");
  private static final EventResponse 모바일_컨퍼런스 = new EventResponse(null, "모바일 컨퍼런스", null, null,
      List.of(), "진행 예정");
  private static final LocalDate TODAY = LocalDate.of(2023, 7, 21);

  @Autowired
  private EventService eventService;

  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private EventTagRepository eventTagRepository;
  @Autowired
  private TagRepository tagRepository;

  @BeforeEach
  void init() {
    Tag 백엔드 = tagRepository.save(백엔드());
    Tag 프론트엔드 = tagRepository.save(프론트엔드());
    Tag 안드로이드 = tagRepository.save(안드로이드());
    Tag IOS = tagRepository.save(IOS());
    Tag AI = tagRepository.save(AI());

    Event 인프콘_2023 = eventRepository.save(인프콘_2023());
    Event AI_컨퍼런스 = eventRepository.save(AI_컨퍼런스());
    Event 모바일_컨퍼런스 = eventRepository.save(모바일_컨퍼런스());
    Event 안드로이드_컨퍼런스 = eventRepository.save(안드로이드_컨퍼런스());
    Event 웹_컨퍼런스 = eventRepository.save(웹_컨퍼런스());

    eventTagRepository.saveAll(List.of(
        new EventTag(인프콘_2023, 백엔드), new EventTag(인프콘_2023, 프론트엔드), new EventTag(인프콘_2023, 안드로이드),
        new EventTag(인프콘_2023, IOS), new EventTag(인프콘_2023, AI), new EventTag(AI_컨퍼런스, AI),
        new EventTag(모바일_컨퍼런스, 안드로이드), new EventTag(모바일_컨퍼런스, IOS), new EventTag(안드로이드_컨퍼런스, 안드로이드),
        new EventTag(웹_컨퍼런스, 백엔드), new EventTag(웹_컨퍼런스, 프론트엔드))
    );
  }

  @Nested
  @DisplayName("id로 이벤트를 조회할 수 있다.")
  class findEventTest {

    @Test
    @DisplayName("event의 id로 해당하는 event를 조회할 수 있다.")
    void success() {
      //given
      final Event event = eventRepository.save(eventFixture());
      final EventDetailResponse expected = EventDetailResponse.from(event);

      //when
      final EventDetailResponse actual = eventService.findEvent(event.getId());

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
      assertThatThrownBy(() -> eventService.findEvent(notFoundEventId))
          .isInstanceOf(EventException.class)
          .hasMessage(EventExceptionType.NOT_FOUND_EVENT.errorMessage());
    }
  }

  @Test
  @DisplayName("event의 id로 참여자 목록을 조회할 수 있다.")
  void findParticipants() {
    // given
    final Event 인프콘 = eventRepository.save(eventFixture());
    final Member 멤버1 = memberRepository.save(new Member(123L, "image1.com", "멤버1"));
    final Member 멤버2 = memberRepository.save(new Member(124L, "image2.com", "멤버2"));

    final Long 멤버1_참가자_ID = eventService.participate(인프콘.getId(), 멤버1.getId(), 멤버1);
    final Long 멤버2_참가자_ID = eventService.participate(인프콘.getId(), 멤버2.getId(), 멤버2);

    //when
    final List<ParticipantResponse> actual = eventService.findParticipants(인프콘.getId());

    final List<ParticipantResponse> expected = List.of(
        new ParticipantResponse(멤버1_참가자_ID, 멤버1.getId(), 멤버1.getName(), 멤버1.getImageUrl(),
            멤버1.getDescription()),
        new ParticipantResponse(멤버2_참가자_ID, 멤버2.getId(), 멤버2.getName(), 멤버2.getImageUrl(),
            멤버2.getDescription())
    );
    //then
    assertThat(actual)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrderElementsOf(expected);
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

      final Long participantId = eventService.participate(인프콘.getId(), memberId, member);

      assertThat(participantId)
          .isNotNull();
    }

    @Test
    @DisplayName("memberId와 Member가 다르면 Exception이 발생한다.")
    void fail_forbidden() {
      final Long memberId = 1L;
      final Long otherMemberId = 2L;
      final Member member = memberRepository.findById(memberId).get();
      final Event 인프콘 = eventRepository.save(eventFixture());

      assertThatThrownBy(() -> eventService.participate(인프콘.getId(), otherMemberId, member))
          .isInstanceOf(EventException.class)
          .hasMessage(EventExceptionType.FORBIDDEN_PARTICIPATE_EVENT.errorMessage());
    }

    @Test
    @DisplayName("이미 참가한 멤버면 Exception이 발생한다.")
    void fail_alreadyParticipate() {
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();
      final Event 인프콘 = eventRepository.save(eventFixture());
      eventService.participate(인프콘.getId(), memberId, member);

      assertThatThrownBy(() -> eventService.participate(인프콘.getId(), memberId, member))
          .isInstanceOf(EventException.class)
          .hasMessage(EventExceptionType.ALREADY_PARTICIPATED.errorMessage());
    }
  }

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
