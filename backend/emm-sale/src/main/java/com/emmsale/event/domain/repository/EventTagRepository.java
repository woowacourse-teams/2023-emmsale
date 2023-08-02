package com.emmsale.event.domain.repository;

import com.emmsale.event.domain.EventTag;
import com.emmsale.tag.domain.Tag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTagRepository extends JpaRepository<EventTag, Long> {

  List<EventTag> findEventTagsByTag(Tag tag);

  void deleteAllByEventId(Long eventId);
}
