package com.emmsale.comment.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Query("select c From Comment c inner join Event e on c.event.id = e.id where e.id = :eventId")
  List<Comment> findByEventId(@Param("eventId") final Long eventId);

  List<Comment> findByParentId(Long parentId);
}
