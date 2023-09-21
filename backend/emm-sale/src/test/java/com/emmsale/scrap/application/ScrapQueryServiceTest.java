package com.emmsale.scrap.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.event.EventFixture;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.scrap.domain.Scrap;
import com.emmsale.scrap.domain.ScrapRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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

  @Test
  @DisplayName("전체 스크랩 목록을 조회한다.")
  void findAllScrapsTest() {
    //given
    final Member member = memberRepository.findById(1L).get();

    final Event event1 = eventRepository.save(EventFixture.인프콘_2023());
    final Event event2 = eventRepository.save(EventFixture.구름톤());

    scrapRepository.save(new Scrap(member.getId(), event1));
    scrapRepository.save(new Scrap(member.getId(), event2));

    //when
    final List<EventResponse> actual = scrapQueryService.findAllScraps(member);

    final List<EventResponse> expected = List.of(
        new EventResponse(event1.getId(), event1.getName(), event1.getEventPeriod().getStartDate(),
            event1.getEventPeriod().getEndDate(),
            Collections.emptyList(), "ENDED", "ENDED", event1.getImageUrl(), event1.getEventPeriod()
            .calculateRemainingDays(LocalDate.now()), event1.getEventPeriod()
            .calculateApplyRemainingDays(LocalDate.now()), event1
            .getEventMode().getValue(), event1.getPaymentType().getValue(), "인프런"),

        new EventResponse(event2.getId(), event2.getName(), event2.getEventPeriod().getStartDate(),
            event2.getEventPeriod().getEndDate(),
            Collections.emptyList(), "ENDED", "ENDED", event2.getImageUrl(), event2.getEventPeriod()
            .calculateRemainingDays(LocalDate.now()), event2.getEventPeriod()
            .calculateApplyRemainingDays(LocalDate.now()), event2
            .getEventMode().getValue(), event2.getPaymentType().getValue(), "행사기관")
    );
    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

}
