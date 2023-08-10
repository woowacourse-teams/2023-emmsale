package com.emmsale.event.application.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventDetailResponseTest {

  @Test
  @DisplayName("이벤트를 상세 조회할 값을 변환할 수 있다.")
  void createEventDetailResponseTest() {
    //given
    final Event 구름톤 = EventFixture.구름톤();
    final LocalDate 날짜 = LocalDate.of(2023, 7, 1);
    final EventDetailResponse expected = new EventDetailResponse(
        구름톤.getId(),
        구름톤.getName(),
        구름톤.getInformationUrl(),
        구름톤.getEventPeriod().getStartDate(),
        구름톤.getEventPeriod().getEndDate(),
        구름톤.getEventPeriod().getApplyStartDate(),
        구름톤.getEventPeriod().getApplyEndDate(),
        구름톤.getLocation(),
        EventStatus.UPCOMING.name(),
        EventStatus.UPCOMING.name(),
        Collections.emptyList(),
        구름톤.getImageUrl(),
        2, 2,
        구름톤.getType().toString()
    );

    //when
    final EventDetailResponse actual = EventDetailResponse.from(구름톤, 날짜);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }
}
