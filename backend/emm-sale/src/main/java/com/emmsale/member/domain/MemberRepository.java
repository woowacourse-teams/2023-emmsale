package com.emmsale.member.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByGithubId(Long githubId);

  @Query("select count(*) from Member m where m.id in :ids")
  Long countMembersById(@Param("ids") final List<Long> ids);
}
