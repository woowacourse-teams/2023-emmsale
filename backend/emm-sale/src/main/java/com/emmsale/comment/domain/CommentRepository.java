package com.emmsale.comment.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Query("select c From Comment c inner join Event e on c.event.id = e.id where e.id = :eventId")
  List<Comment> findByEventId(@Param("eventId") final Long eventId);

  @Query("select c1 From Comment c1 left outer join c1.parent p where c1.id=:id or p.id=:id")
  List<Comment> findParentAndChildrenByParentId(@Param("id") final Long commentId);

  @Query("select c From Comment c join fetch c.member m where m.id = :memberId")
  List<Comment> findByMemberId(@Param("memberId") final Long memberId);
}
