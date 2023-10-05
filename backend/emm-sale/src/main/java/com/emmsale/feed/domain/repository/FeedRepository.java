package com.emmsale.feed.domain.repository;

import com.emmsale.feed.domain.Feed;
import com.emmsale.member.domain.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {

  @Query("select f from Feed f where f.event.id = :eventId and f.isDeleted = false")
  List<Feed> findAllByEventIdAndNotDeleted(Long eventId);

  @Query("select f from Feed f where f.writer = :member and f.isDeleted = false")
  List<Feed> findByMember(Member member);
}
