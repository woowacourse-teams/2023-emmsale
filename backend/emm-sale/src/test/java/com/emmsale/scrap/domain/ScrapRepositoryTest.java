package com.emmsale.scrap.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql("/data-test.sql")
class ScrapRepositoryTest {

  @Autowired
  private ScrapRepository scrapRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private MemberRepository memberRepository;

  private Member 사용자;
  private Event 행사1;
  private Event 행사2;

  @BeforeEach
  void setUp() {
    사용자 = memberRepository.findById(1L).get();
    행사1 = eventRepository.save(EventFixture.구름톤());
    행사2 = eventRepository.save(EventFixture.인프콘_2023());
  }

  @Test
  @DisplayName("사용자의 id와 행사의 id로 스크랩이 있는지 확인한다.")
  void existsByMemberIdAndEventIdTest() {
    //given
    scrapRepository.save(new Scrap(사용자.getId(), 행사1));

    //when & then
    assertTrue(scrapRepository.existsByMemberIdAndEventId(사용자.getId(), 행사1.getId()));
    assertFalse(scrapRepository.existsByMemberIdAndEventId(사용자.getId(), 행사2.getId()));
  }

  @Test
  @DisplayName("사용자의 id로 해당 사용자가 가진 모든 행사를 조회한다.")
  void findAllByMemberIdTest() {
    //given
    scrapRepository.save(new Scrap(사용자.getId(), 행사1));
    scrapRepository.save(new Scrap(사용자.getId(), 행사2));

    //when
    final List<Scrap> scraps = scrapRepository.findAllByMemberId(사용자.getId());

    //then
    assertThat(scraps)
        .extracting("event")
        .containsExactly(행사1, 행사2);
  }
}
