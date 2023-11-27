package com.emmsale.scrap.application;

import static com.emmsale.image.ImageFixture.행사_이미지1;
import static com.emmsale.image.ImageFixture.행사_이미지2;
import static com.emmsale.image.ImageFixture.행사_이미지3;
import static com.emmsale.image.ImageFixture.행사_이미지4;
import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.event.EventFixture;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventTag;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.image.domain.repository.ImageRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.scrap.domain.Scrap;
import com.emmsale.scrap.domain.ScrapRepository;
import com.emmsale.tag.domain.Tag;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ScrapQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private ScrapQueryService scrapQueryService;
  @Autowired
  private ScrapRepository scrapRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private ImageRepository imageRepository;

  @Test
  @DisplayName("전체 스크랩 목록을 조회한다.")
  void findAllScrapsTest() {
    //given
    final Member member = memberRepository.findById(1L).get();

    final Event event1 = eventRepository.save(EventFixture.인프콘_2023());
    final Event event2 = eventRepository.save(EventFixture.구름톤());
    imageRepository.saveAll(
        List.of(행사_이미지1(event1.getId()), 행사_이미지2(event1.getId()), 행사_이미지3(event1.getId()),
            행사_이미지4(event1.getId())));
    scrapRepository.save(new Scrap(member.getId(), event1));
    scrapRepository.save(new Scrap(member.getId(), event2));

    //when
    final List<EventResponse> actual = scrapQueryService.findAllScraps(member);

    final List<EventResponse> expected = List.of(
        new EventResponse(
            event1.getId(), event1.getName(), event1.getInformationUrl(),
            event1.getEventPeriod().getStartDate(), event1.getEventPeriod().getEndDate(),
            event1.getEventPeriod().getApplyStartDate(), event1.getEventPeriod().getApplyEndDate(),
            event1.getLocation(),
            event1.getTags()
                .stream()
                .map(EventTag::getTag)
                .map(Tag::getName)
                .collect(Collectors.toList()),
            행사_이미지1(event1.getId()).getName(), event1.getType().name(),
            List.of(행사_이미지2(event1.getId()).getName(), 행사_이미지3(event1.getId()).getName(),
                행사_이미지4(event1.getId()).getName()),
            event1.getOrganization(), event1.getPaymentType().getValue(),
            event1.getEventMode().getValue()),
        new EventResponse(event2.getId(), event2.getName(), event2.getInformationUrl(),
            event2.getEventPeriod().getStartDate(), event2.getEventPeriod().getEndDate(),
            event2.getEventPeriod().getApplyStartDate(), event2.getEventPeriod().getApplyEndDate(),
            event2.getLocation(),
            event2.getTags()
                .stream()
                .map(EventTag::getTag)
                .map(Tag::getName)
                .collect(Collectors.toList()),
            null, event2.getType().name(),
            List.of(),
            event2.getOrganization(), event2.getPaymentType().getValue(),
            event2.getEventMode().getValue())
    );

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

}
