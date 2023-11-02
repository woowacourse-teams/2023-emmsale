package com.emmsale.event.application.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventMode;
import com.emmsale.event.domain.PaymentType;
import com.emmsale.image.domain.AllImagesOfContent;
import com.emmsale.image.domain.Image;
import com.emmsale.image.domain.ImageType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventResponseTest {

  @Test
  @DisplayName("이벤트를 상세 조회할 값을 변환할 수 있다.")
  void createEventDetailResponseTest() {
    //given
    final Event 구름톤 = EventFixture.구름톤();
    final String thumbnailUrl = "thumbnail";
    final String imageUrl1 = "imageUrl1";
    final String imageUrl2 = "imageUrl2";
    final List<String> imageUrls = List.of(imageUrl1, imageUrl2);
    final AllImagesOfContent images = new AllImagesOfContent(
        List.of(
            new Image(thumbnailUrl, ImageType.EVENT, 구름톤.getId(), 0, null),
            new Image(imageUrl1, ImageType.EVENT, 구름톤.getId(), 1, null),
            new Image(imageUrl2, ImageType.EVENT, 구름톤.getId(), 2, null)
        )
    );

    final EventResponse expected = new EventResponse(
        구름톤.getId(),
        구름톤.getName(),
        구름톤.getInformationUrl(),
        구름톤.getEventPeriod().getStartDate(),
        구름톤.getEventPeriod().getEndDate(),
        구름톤.getEventPeriod().getApplyStartDate(),
        구름톤.getEventPeriod().getApplyEndDate(),
        구름톤.getLocation(),
        Collections.emptyList(),
        thumbnailUrl,
        구름톤.getType().toString(),
        imageUrls,
        구름톤.getOrganization(),
        PaymentType.FREE_PAID.getValue(),
        EventMode.ON_OFFLINE.getValue()
    );

    //when
    final EventResponse actual = EventResponse.from(구름톤, images);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }
}
