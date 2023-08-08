package com.emmsale.event.domain;

import static com.emmsale.event.EventFixture.eventFixture;
import static com.emmsale.event.exception.EventExceptionType.FORBIDDEN_UPDATE_PARTICIPATE;
import static com.emmsale.event.exception.EventExceptionType.PARTICIPANT_NOT_BELONG_EVENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.emmsale.event.exception.EventException;
import com.emmsale.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ParticipantTest {

  private Member member;
  private Event event;

  @BeforeEach
  void setUp() {
    member = new Member(1L, 2L, "이미지URL", "멤버");
    event = spy(eventFixture());
    when(event.getId()).thenReturn(1L);
  }

  @Test
  @DisplayName("content를 업데이트할 수 있다.")
  void updateContent() {
    //given
    final Participant participant = new Participant(member, event, "변경 전");
    final String contentRequest = "변경 후";

    //when
    participant.updateContent(contentRequest);

    //then
    assertThat(participant.getContent())
        .isEqualTo(contentRequest);
  }

  @Nested
  @DisplayName("참가 게시글의 소유자를 검증할 수 있다.")
  class ValidateOwner {

    @Test
    @DisplayName("입력 member와 participant의 member가 같은 경우")
    void success() {
      //given
      final Participant participant = new Participant(member, event, "내용");

      //when && then
      assertDoesNotThrow(() -> participant.validateOwner(member));
    }

    @Test
    @DisplayName("입력 member와 participant의 member가 다른 경우")
    void invalidMemberException() {
      //given
      final Participant participant = new Participant(member, event, "content");
      final Member member = new Member(2L, 3L, "이미지", "다른멤버");

      //when && then
      assertThatThrownBy(() -> participant.validateOwner(member))
          .isInstanceOf(EventException.class)
          .hasMessage(FORBIDDEN_UPDATE_PARTICIPATE.errorMessage());
    }
  }

  @Nested
  @DisplayName("참가 게시글이 Event에 속한 정보인지 확인할 수 있다.")
  class ValidateEvent {

    @Test
    @DisplayName("participant의 eventId가 입력값과 같은 경우")
    void success() {
      //given
      final Participant participant = new Participant(member, event, "내용");

      //when && then
      assertDoesNotThrow(() -> participant.validateEvent(event.getId()));
    }

    @Test
    @DisplayName("participant의 eventId가 입려값과 다른 경우")
    void invalidEventIdException() {
      //given
      final Participant participant = new Participant(member, event, "content");

      //when && then
      assertThatThrownBy(() -> participant.validateEvent(event.getId() + 1))
          .isInstanceOf(EventException.class)
          .hasMessage(PARTICIPANT_NOT_BELONG_EVENT.errorMessage());
    }
  }
}
