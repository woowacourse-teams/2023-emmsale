package com.emmsale.member.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByGithubId(Long githubId);

  @Query("select count(*) from Member m where m.id in :ids")
  Long countMembersById(@Param("ids") final List<Long> ids);

  boolean existsById(@NotNull final Long id);

  @Query("select m from Member m where m.id in :memberIds")
  List<Member> findAllByIdIn(@Param("memberIds") final Set<Long> memberIds);
}
