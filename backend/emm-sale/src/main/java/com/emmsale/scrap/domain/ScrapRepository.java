package com.emmsale.scrap.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

  boolean existsByMemberIdAndEventId(Long memberId, Long eventId);
}
