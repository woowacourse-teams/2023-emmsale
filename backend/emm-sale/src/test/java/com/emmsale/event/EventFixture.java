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
}
