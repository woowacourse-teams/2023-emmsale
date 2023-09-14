package com.emmsale.comment.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Query("select c From Comment c inner join Feed f on c.feed.id = f.id where f.id = :feedId")
  List<Comment> findByFeedId(@Param("feedId") final Long feedId);

  @Query("select c1 From Comment c1 "
      + "left outer join fetch c1.parent p "
      + "join fetch c1.member "
      + "where c1.id=:id or p.id=:id")
  List<Comment> findParentAndChildrenByParentId(@Param("id") final Long commentId);

  @Query("select c From Comment c join fetch c.member m where m.id = :memberId")
  List<Comment> findByMemberId(@Param("memberId") final Long memberId);
}
