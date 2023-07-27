package com.emmsale.event.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.event.EventFixture;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.member.domain.Member;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EventTest {

  @Nested
  class addParticipant {

    @Test
    @DisplayName("Event에 Member를 추가할 수 있다.")
    void success() {
      //given
      final Event 인프콘 = EventFixture.eventFixture();
      final Member 멤버 = new Member(1L, "이미지URL", "멤버");

      //when
      인프콘.addParticipant(멤버);

      //then
      final List<Member> members = 인프콘.getParticipants().stream()
          .map(Participant::getMember)
          .collect(Collectors.toList());
      assertThat(members)
          .usingRecursiveFieldByFieldElementComparator()
          .containsExactlyInAnyOrder(멤버);
    }

    @Test
    @DisplayName("Event에 Member가 이미 포함되어 있으면 Exception 발생")
    void fail_alreadyContains() {
      //given
      final Event 인프콘 = EventFixture.eventFixture();
      final Member 멤버 = new Member(1L, 1L, "이미지URL", "멤버");
      인프콘.addParticipant(멤버);

      //when && then
      assertThatThrownBy(() -> 인프콘.addParticipant(멤버))
          .isInstanceOf(EventException.class)
          .hasMessage(EventExceptionType.ALREADY_PARTICIPATED.errorMessage());
    }
  }
}
