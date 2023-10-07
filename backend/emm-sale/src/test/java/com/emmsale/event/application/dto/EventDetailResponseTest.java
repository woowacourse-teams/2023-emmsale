package com.emmsale.event.application.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.PaymentType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventDetailResponseTest {

  @Test
  @DisplayName("이벤트를 상세 조회할 값을 변환할 수 있다.")
  void createEventDetailResponseTest() {
    //given
    final Event 구름톤 = EventFixture.구름톤();
    final String imageUrl = "thumbnail";
    final List<String> imageUrls = List.of("imageUrl1", "imageUrl2");

    final EventDetailResponse expected = new EventDetailResponse(
        구름톤.getId(),
        구름톤.getName(),
        구름톤.getInformationUrl(),
        구름톤.getEventPeriod().getStartDate(),
        구름톤.getEventPeriod().getEndDate(),
        구름톤.getEventPeriod().getApplyStartDate(),
        구름톤.getEventPeriod().getApplyEndDate(),
        구름톤.getLocation(),
        Collections.emptyList(),
        imageUrl,
        구름톤.getType().toString(),
        imageUrls,
        구름톤.getOrganization(),
        PaymentType.FREE_PAID.getValue()
    );

    //when
    final EventDetailResponse actual = EventDetailResponse.from(구름톤, imageUrl, imageUrls);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }
}
