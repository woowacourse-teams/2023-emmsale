package com.emmsale.scrap.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

  boolean existsByMemberIdAndEventId(Long memberId, Long eventId);

  List<Scrap> findAllByMemberId(@Param("memberId") Long memberId);

  void deleteByMemberIdAndEventId(Long memberId, Long eventId);
}
