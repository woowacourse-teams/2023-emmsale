package com.emmsale.scrap.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.scrap.application.dto.ScrapRequest;
import com.emmsale.scrap.domain.Scrap;
import com.emmsale.scrap.domain.ScrapRepository;
import com.emmsale.scrap.exception.ScrapException;
import com.emmsale.scrap.exception.ScrapExceptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ScrapCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private ScrapCommandService scrapCommandService;
  @Autowired
  private ScrapRepository scrapRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private MemberRepository memberRepository;

  private Member member;
  private Event event;

  @BeforeEach
  void setUp() {
    member = memberRepository.findById(1L).get();
    event = eventRepository.save(EventFixture.구름톤());
  }

  @Test
  @DisplayName("스크랩을 정상적으로 삭제한다.")
  void append() {
    //given
    final Scrap scrap = scrapRepository.save(new Scrap(member.getId(), event));

    //when
    scrapCommandService.deleteScrap(member, event.getId());

    //then
    assertFalse(scrapRepository.findById(scrap.getId()).isPresent());
  }

  @Nested
  @DisplayName("스크랩을 추가하는 테스트")
  class AppendScrap {

    @Test
    @DisplayName("스크랩을 정상적으로 추가한다.")
    void append() {
      //given

      final ScrapRequest request = new ScrapRequest(event.getId());

      //when
      scrapCommandService.append(member, request);

      //then
      assertTrue(scrapRepository.existsByMemberIdAndEventId(member.getId(), event.getId()));
    }

    @Test
    @DisplayName("이미 스크랩한 이벤트일 경우 ALREADY_EXIST_SCRAP 타입의 ScrapException이 발생한다.")
    void appendWithAlreadyScraped() {
      //given
      scrapRepository.save(new Scrap(member.getId(), event));

      final ScrapRequest request = new ScrapRequest(event.getId());

      final ScrapExceptionType expectExceptionType = ScrapExceptionType.ALREADY_EXIST_SCRAP;

      //when
      final ScrapException actualException = assertThrowsExactly(ScrapException.class,
          () -> scrapCommandService.append(member, request));

      //then
      assertEquals(expectExceptionType, actualException.exceptionType());
    }

    @Test
    @DisplayName("스크랩할 이벤트가 존재하지 않을 경우 NOT_FOUND_EVENT 타입의 EventException이 발생한다.")
    void appendWithNotExistEvent() {
      //given
      final long 존재하지_않는_이벤트_id = 0L;

      final ScrapRequest request = new ScrapRequest(존재하지_않는_이벤트_id);

      final EventExceptionType expectExceptionType = EventExceptionType.NOT_FOUND_EVENT;

      //when
      final EventException actualException = assertThrowsExactly(EventException.class,
          () -> scrapCommandService.append(member, request));

      //then
      assertEquals(expectExceptionType, actualException.exceptionType());
    }

  }
}
