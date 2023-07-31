package com.emmsale.event.domain.repository;

import com.emmsale.event.domain.Participant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

  @Query("select p from Participant p where p.member.id= :memberId and p.event.id= :eventId")
  List<Participant> findByMemberIdAndEventId(@Param("memberId") final Long memberId,
      @Param("eventId") final Long eventId);
}
