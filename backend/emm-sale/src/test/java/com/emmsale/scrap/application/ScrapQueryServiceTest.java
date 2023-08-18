package com.emmsale.scrap.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.scrap.application.dto.ScrapResponse;
import com.emmsale.scrap.domain.Scrap;
import com.emmsale.scrap.domain.ScrapRepository;
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
    final List<ScrapResponse> scraps = scrapQueryService.findAllScraps(member);

    //then
    assertThat(scraps).extracting("eventId")
        .containsExactly(event1.getId(), event2.getId());
  }

}
