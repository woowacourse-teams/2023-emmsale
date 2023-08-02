package com.emmsale.event.domain.repository;

import static com.emmsale.event.EventFixture.인프콘_2023;
import static com.emmsale.member.MemberFixture.memberFixture;
import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.Participant;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@Sql("/data-test.sql")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
class ParticipantRepositoryTest {

  @Autowired
  private ParticipantRepository participantRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private EventRepository eventRepository;

  @Nested
  @DisplayName("eventId와 memberId로 Participant가 존재하는 지 확인할 수 있다.")
  class ExistsByEventIdAndMemberId {

    @Test
    @DisplayName("Participant가 존재하는 경우 true를 반환한다.")
    void alreadyParticipateThenTrue() {
      //given
      final Event 인프콘 = eventRepository.save(인프콘_2023());
      final Member 멤버 = memberRepository.save(memberFixture());
      participantRepository.save(new Participant(멤버, 인프콘));

      //when
      final Boolean actual = participantRepository.existsByEventIdAndMemberId(인프콘.getId(),
          멤버.getId());

      //then
      assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Particpant가 존재하지 않는 false를 반환한다.")
    void isNotAlreadyParticipateThenFalse() {
      //given
      final Event 인프콘 = eventRepository.save(인프콘_2023());
      final Member 멤버 = memberRepository.save(memberFixture());

      //when
      final Boolean actual = participantRepository.existsByEventIdAndMemberId(인프콘.getId(),
          멤버.getId());

      //then
      assertThat(actual).isFalse();
    }
  }
}
