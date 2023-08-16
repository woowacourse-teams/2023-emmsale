package com.emmsale.member.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterestTagRepository extends JpaRepository<InterestTag, Long> {

  List<InterestTag> findInterestTagsByMemberId(final Long memberId);

  boolean existsByTagIdIn(List<Long> tagIds);

  @Query("select it from InterestTag it "
      + "where it.member = :member "
      + "and it.tag.id in :deleteTagId")
  List<InterestTag> findAllByMemberAndTagIds(
      @Param("member") final Member member,
      @Param("deleteTagId") final List<Long> deleteTagId);

  void deleteAllByMember(Member member);
}
