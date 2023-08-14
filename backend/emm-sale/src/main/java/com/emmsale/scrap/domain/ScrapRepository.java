package com.emmsale.scrap.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

  boolean existsByMemberIdAndEventId(Long memberId, Long eventId);

  @Query("select s from Scrap s where s.memberId = :memberId")
  List<Scrap> findAllByMemberId(@Param("memberId") Long memberId);

}
