package com.emmsale.event;

import com.emmsale.event.domain.Event;
import java.time.LocalDateTime;

public class EventFixture {

  public static Event eventFixture() {
    return new Event(
        "인프콘 2023",
        "코엑스",
        LocalDateTime.of(2023, 8, 15, 15, 0),
        LocalDateTime.of(2023, 8, 15, 15, 0),
        "http://infcon.com"
    );
  }

  public static Event 인프콘_2023() {
    return new Event("인프콘 2023", "코엑스", LocalDateTime.parse("2023-06-01T12:00:00"),
        LocalDateTime.parse("2023-09-01T12:00:00"), "https://~~~");
  }

  public static Event AI_컨퍼런스() {
    return new Event("AI 컨퍼런스", "코엑스", LocalDateTime.parse("2023-07-22T12:00:00"),
        LocalDateTime.parse("2023-07-30T12:00:00"), "https://~~~");
  }

  public static Event 모바일_컨퍼런스() {
    return new Event("모바일 컨퍼런스", "코엑스", LocalDateTime.parse("2023-08-03T12:00:00"),
        LocalDateTime.parse("2023-09-03T12:00:00"), "https://~~~");
  }

  public static Event 안드로이드_컨퍼런스() {
    return new Event("안드로이드 컨퍼런스", "코엑스", LocalDateTime.parse("2023-06-29T12:00:00"),
        LocalDateTime.parse("2023-07-16T12:00:00"), "https://~~~");
  }

  public static Event 웹_컨퍼런스() {
    return new Event("웹 컨퍼런스", "코엑스", LocalDateTime.parse("2023-07-03T12:00:00"),
        LocalDateTime.parse("2023-08-03T12:00:00"), "https://~~~");
  }

}
