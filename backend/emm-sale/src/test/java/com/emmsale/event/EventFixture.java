package com.emmsale.event;

import com.emmsale.event.application.dto.RecruitmentPostRequest;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventMode;
import com.emmsale.event.domain.EventType;
import com.emmsale.event.domain.PaymentType;
import com.emmsale.member.domain.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventFixture {

  public static Event eventFixture() {
    return new Event(
        "인프콘 2023",
        "코엑스",
        LocalDateTime.of(2023, 8, 15, 15, 0),
        LocalDateTime.of(2023, 8, 15, 15, 0),
        LocalDateTime.of(2023, 8, 15, 15, 0),
        LocalDateTime.of(2023, 8, 15, 15, 0),
        "http://infcon.com",
        EventType.CONFERENCE,
        PaymentType.PAID,
        EventMode.OFFLINE,
        "인프런"
    );
  }

  public static Event 인프콘_2023() {
    return new Event("인프콘 2023", "코엑스", LocalDateTime.parse("2023-06-01T12:00:00"),
        LocalDateTime.parse("2023-09-01T12:00:00"), LocalDateTime.parse("2023-05-01T12:00:00"),
        LocalDateTime.parse("2023-06-01T12:00:00"), "https://~~~", EventType.CONFERENCE,
        PaymentType.FREE_PAID, EventMode.ON_OFFLINE, "인프런"
    );
  }

  public static Event AI_컨퍼런스() {
    return new Event("AI 컨퍼런스", "코엑스", LocalDateTime.parse("2023-07-22T12:00:00"),
        LocalDateTime.parse("2023-07-30T12:00:00"), LocalDateTime.parse("2023-07-01T12:00:00"),
        LocalDateTime.parse("2023-07-22T12:00:00"), "https://~~~", EventType.CONFERENCE,
        PaymentType.FREE_PAID, EventMode.ON_OFFLINE, "행사기관"
    );
  }

  public static Event 모바일_컨퍼런스() {
    return new Event("모바일 컨퍼런스", "코엑스", LocalDateTime.parse("2023-08-03T12:00:00"),
        LocalDateTime.parse("2023-09-03T12:00:00"), LocalDateTime.parse("2023-08-01T12:00:00"),
        LocalDateTime.parse("2023-08-02T12:00:00"), "https://~~~", EventType.CONFERENCE,
        PaymentType.FREE_PAID, EventMode.ON_OFFLINE, "행사기관"
    );
  }

  public static Event 안드로이드_컨퍼런스() {
    return new Event("안드로이드 컨퍼런스", "코엑스", LocalDateTime.parse("2023-06-29T12:00:00"),
        LocalDateTime.parse("2023-07-16T12:00:00"), LocalDateTime.parse("2023-06-01T12:00:00"),
        LocalDateTime.parse("2023-06-20T12:00:00"), "https://~~~", EventType.CONFERENCE,
        PaymentType.FREE_PAID, EventMode.ON_OFFLINE, "행사기관"
    );
  }

  public static Event 웹_컨퍼런스() {
    return new Event("웹 컨퍼런스", "코엑스", LocalDateTime.parse("2023-07-03T12:00:00"),
        LocalDateTime.parse("2023-08-03T12:00:00"), LocalDateTime.parse("2023-07-03T12:00:00"),
        LocalDateTime.parse("2023-08-03T12:00:00"), "https://~~~", EventType.CONFERENCE,
        PaymentType.FREE_PAID, EventMode.ON_OFFLINE, "행사기관"
    );
  }

  public static Event AI_아이디어_공모전() {
    return new Event("AI 아이디어 공모전", "코엑스", LocalDateTime.parse("2023-06-29T12:00:00"),
        LocalDateTime.parse("2023-07-16T12:00:00"), LocalDateTime.parse("2023-06-01T12:00:00"),
        LocalDateTime.parse("2023-07-16T12:00:00"), "https://~~~",
        EventType.COMPETITION, PaymentType.FREE_PAID, EventMode.ON_OFFLINE, "행사기관"
    );
  }

  public static Event 구름톤() {
    return new Event("구름톤", "코엑스", LocalDateTime.parse("2023-07-03T12:00:00"),
        LocalDateTime.parse("2023-08-03T12:00:00"), LocalDateTime.parse("2023-07-03T12:00:00"),
        LocalDateTime.parse("2023-08-03T12:00:00"), "https://~~~",
        EventType.COMPETITION, PaymentType.FREE_PAID, EventMode.ON_OFFLINE, "행사기관"
    );
  }

  public static LocalDate 날짜_8월_10일() {
    return LocalDate.of(2023, 8, 10);
  }

  public static RecruitmentPostRequest createRecruitmentPostRequest(final Member member) {
    return new RecruitmentPostRequest(member.getId(), "같이 가요 요청 글");
  }
}
