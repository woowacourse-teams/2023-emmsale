package com.emmsale.event.domain.repository;

import com.emmsale.event.domain.RecruitmentPost;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitmentPostRepository extends JpaRepository<RecruitmentPost, Long> {

  Optional<RecruitmentPost> findByMemberIdAndEventId(final Long memberId, final Long eventId);

  Boolean existsByEventIdAndMemberId(final Long eventId, final Long memberId);
}
