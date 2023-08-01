package com.emmsale.event.domain.repository;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

  List<Event> findEventsByType(final EventType type);

  boolean existsById(final Long eventId);
}
