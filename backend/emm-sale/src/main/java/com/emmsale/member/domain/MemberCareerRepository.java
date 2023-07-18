package com.emmsale.member.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberCareerRepository extends JpaRepository<MemberCareer, Long> {

  @Query("select mc from MemberCareer mc where mc.member.id = :memberId")
  List<MemberCareer> findAllByMemberId(@Param("memberId") final Long memberId);
}
