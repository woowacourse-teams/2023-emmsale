package com.emmsale.feed.domain.repository;

import com.emmsale.event.domain.Event;
import com.emmsale.feed.domain.Feed;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {

  List<Feed> findAllByEvent(Event event);
}
