package com.emmsale.event.domain.repository;

import com.emmsale.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>,
    JpaSpecificationExecutor<Event> {

  boolean existsById(final Long eventId);
}
