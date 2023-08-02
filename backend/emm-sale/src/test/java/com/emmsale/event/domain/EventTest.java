package com.emmsale.event.domain;

import static com.emmsale.event.EventFixture.eventFixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import com.emmsale.event.EventFixture;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.member.domain.Member;
import com.emmsale.tag.TagFixture;
import com.emmsale.tag.domain.Tag;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EventTest {

  @ParameterizedTest
  @CsvSource(value = {"2023-03-01,UPCOMING", "2023-07-25,IN_PROGRESS",
      "2023-08-01,ENDED"}, delimiter = ',')
  @DisplayName("현재 날짜가 주어지면 행사의 진행 상태를 계산한다.")
  void calculateEventStatus(LocalDate input, EventStatus expected) {
    // given, when
    EventStatus actual = EventFixture.AI_컨퍼런스().calculateEventStatus(input);

    // then
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @DisplayName("Event 생성시 startDate가 endDate 이후일 경우 EventException이 발생한다.")
  void newEventWithStartDateAfterEndDateTest() {
    //given
    final String name = "이름";
    final String location = "장소";
    final String url = "https://information-url.com";
    final LocalDateTime beforeDateTime = LocalDateTime.now();
    final LocalDateTime afterDateTime = beforeDateTime.plusDays(1);
    final String imageUrl = "https://image.com";

    //when & then
    final EventException exception = assertThrowsExactly(EventException.class,
        () -> new Event(name, location, afterDateTime, beforeDateTime, url, EventType.CONFERENCE,
            imageUrl));

    assertEquals(EventExceptionType.START_DATE_TIME_AFTER_END_DATE_TIME, exception.exceptionType());
  }

  @Test
  @DisplayName("Event의 name, location, startDate, endDate, informationUrl, tags를 업데이트할 수 있다.")
  void updateEventContentTest() {
    //given
    final String newName = "새로운 이름";
    final String newLocation = "새로운 장소";
    final LocalDateTime newStartDateTime = LocalDateTime.now();
    final LocalDateTime newEndDateTime = newStartDateTime.plusDays(1);
    final String newInformationUrl = "https://새로운-상세-URL.com";
    final List<Tag> newTags = List.of(TagFixture.IOS(), TagFixture.AI());

    final Event event = EventFixture.인프콘_2023();

    //when
    final Event updatedEvent = event.updateEventContent(
        newName,
        newLocation,
        newStartDateTime,
        newEndDateTime,
        newInformationUrl,
        newTags
    );

    //then
    assertAll(
        () -> assertEquals(newName, updatedEvent.getName()),
        () -> assertEquals(newLocation, updatedEvent.getLocation()),
        () -> assertEquals(newStartDateTime, updatedEvent.getStartDate()),
        () -> assertEquals(newEndDateTime, updatedEvent.getEndDate()),
        () -> assertEquals(newInformationUrl, updatedEvent.getInformationUrl()),
        () -> assertEquals(newTags.size(), event.getTags().size())
    );
  }

  @Test
  @DisplayName("eventContent 업데이트시 startDate가 endDate 이후일 경우 EventException이 발생한다.")
  void updateEventContentWithStartDateAfterEndDateTest() {
    //given
    final String newName = "새로운 이름";
    final String newLocation = "새로운 장소";
    final LocalDateTime beforeDateTime = LocalDateTime.now();
    final LocalDateTime afterDateTime = beforeDateTime.plusDays(1);
    final String newInformationUrl = "https://새로운-상세-URL.com";
    final List<Tag> newTags = List.of(TagFixture.IOS(), TagFixture.AI());

    final Event event = EventFixture.인프콘_2023();

    //when & then
    final EventException exception = assertThrowsExactly(EventException.class,
        () -> event.updateEventContent(newName, newLocation, afterDateTime, beforeDateTime,
            newInformationUrl, newTags));

    assertEquals(EventExceptionType.START_DATE_TIME_AFTER_END_DATE_TIME, exception.exceptionType());
  }

  @Nested
  class addParticipant {

    @Test
    @DisplayName("Event에 Member를 추가할 수 있다.")
    void success() {
      //given
      final Event 인프콘 = eventFixture();
      final Member 멤버 = new Member(1L, 1L, "imageUrl", "멤버");

      //when
      인프콘.addParticipant(멤버);

      //then
      final List<Member> members = 인프콘.getParticipants().stream()
          .map(Participant::getMember)
          .collect(Collectors.toList());
      assertThat(members)
          .usingRecursiveFieldByFieldElementComparator()
          .containsExactlyInAnyOrder(멤버);
    }

    @Test
    @DisplayName("Event에 Member가 이미 포함되어 있으면 Exception 발생")
    void fail_alreadyContains() {
      //given
      final Event 인프콘 = eventFixture();
      final Member 멤버 = new Member(1L, 1L, "이미지URL", "멤버");
      인프콘.addParticipant(멤버);

      //when && then
      assertThatThrownBy(() -> 인프콘.addParticipant(멤버))
          .isInstanceOf(EventException.class)
          .hasMessage(EventExceptionType.ALREADY_PARTICIPATED.errorMessage());
    }
  }

  @Test
  @DisplayName("현재날짜로부터 남은 날짜를 계산할 수 있다.")
  void calculateRemainingDay() {
    //given
    final Event 인프콘 = eventFixture();
    final LocalDate today = LocalDate.of(2023, 8, 10);

    //when
    final int actual = 인프콘.calculateRemainingDays(today);

    //then
    assertThat(actual)
        .isEqualTo(5);
  }
}
