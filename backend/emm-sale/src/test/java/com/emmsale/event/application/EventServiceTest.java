package com.emmsale.event.application;

import static com.emmsale.event.EventFixture.eventFixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EventServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private EventService eventService;
  @Autowired
  private EventRepository eventRepository;

  @Nested
  @DisplayName("id로 이벤트를 조회할 수 있다.")
  class findEventTest {

    @Test
    @DisplayName("event의 id로 해당하는 event를 조회할 수 있다.")
    void success() {
      //given
      final Event event = eventRepository.save(eventFixture());
      final EventDetailResponse expected = EventDetailResponse.from(event);

      //when
      final EventDetailResponse actual = eventService.findEvent(event.getId());

      //then
      assertThat(actual)
          .usingRecursiveComparison()
          .isEqualTo(expected);
    }

    @Test
    @DisplayName("요청한 id에 해당하는 event가 존재하지 않으면 Exception을 던진다.")
    void fail_EventNotFoundException() {
      //given
      final Long notFoundEventId = Long.MAX_VALUE;

      //when, then
      assertThatThrownBy(() -> eventService.findEvent(notFoundEventId))
          .isInstanceOf(EventException.class)
          .hasMessage(EventExceptionType.EVENT_NOT_FOUND_EXCEPTION.errorMessage());
    }
  }

  @Nested
  @DisplayName("event 참가자 목록에 멤버를 추가할 수 있다.")
  class participate {

    @Test
    @DisplayName("정상적으로 멤버를 추가한다.")
    void Success() {
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();
      final Event 인프콘 = eventRepository.save(eventFixture());

      final Long participantId = eventService.participate(인프콘.getId(), memberId, member);

      assertThat(participantId)
          .isNotNull();
    }

    @Test
    @DisplayName("memberId와 Member가 다르면 Exception이 발생한다.")
    void fail_forbidden() {
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();
      final Event 인프콘 = eventRepository.save(eventFixture());

      assertThatThrownBy(() -> eventService.participate(인프콘.getId(), 2L, member))
          .isInstanceOf(EventException.class)
          .hasMessage(EventExceptionType.FORBIDDEN_PARTICIPATE_EVENT.errorMessage());
    }

    @Test
    @DisplayName("이미 참가한 멤버면 Exception이 발생한다.")
    void fail_alreadyParticipate() {
      final Long memberId = 1L;
      final Member member = memberRepository.findById(memberId).get();
      final Event 인프콘 = eventRepository.save(eventFixture());
      eventService.participate(인프콘.getId(), memberId, member);

      assertThatThrownBy(() -> eventService.participate(인프콘.getId(), memberId, member))
          .isInstanceOf(EventException.class)
          .hasMessage(EventExceptionType.ALREADY_PARTICIPATE.errorMessage());
    }

  }
}
