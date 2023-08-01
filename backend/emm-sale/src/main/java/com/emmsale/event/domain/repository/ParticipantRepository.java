package com.emmsale.event.domain.repository;

import com.emmsale.event.domain.Participant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

  Optional<Participant> findByMemberIdAndEventId(final Long memberId, final Long eventId);
}
