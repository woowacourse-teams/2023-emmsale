package com.emmsale.member.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterestTagRepository extends JpaRepository<InterestTag, Long> {
  
  @Query("select it from InterestTag it "
      + "join fetch it.tag "
      + "where it.member.id = :memberId")
  List<InterestTag> findInterestTagsByMemberId(@Param("memberId") final Long memberId);
  
  boolean existsByTagIdIn(List<Long> tagIds);
  
  @Query("select it from InterestTag it "
      + "where it.member = :member "
      + "and it.tag.id in :deleteTagId")
  List<InterestTag> findAllByMemberAndTagIds(
      @Param("member") final Member member,
      @Param("deleteTagId") final List<Long> deleteTagId);
  
  @Query("select it from InterestTag it join fetch it.tag where it.tag.id in :ids")
  List<InterestTag> findInterestTagsByTagIdIn(@Param("ids") final List<Long> ids);
  
  void deleteAllByMember(Member member);
}
