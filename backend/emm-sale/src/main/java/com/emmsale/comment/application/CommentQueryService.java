package com.emmsale.comment.application;

import com.emmsale.base.BaseEntity;
import com.emmsale.comment.application.dto.CommentHierarchyResponse;
import com.emmsale.comment.application.dto.CommentResponse;
import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryService {

  private final CommentRepository commentRepository;

  public List<CommentHierarchyResponse> findAllCommentsByEventId(final Long eventId) {

    final List<Comment> comments = commentRepository.findByEventId(eventId);

    return CommentHierarchyResponse.from(comments);
  }

  public List<CommentResponse> findChildrenComments(final Long parentId) {

    final List<Comment> childrenComments = commentRepository.findByParentId(parentId);

    return childrenComments.stream()
        .sorted(Comparator.comparing(BaseEntity::getCreatedAt))
        .map(CommentResponse::from)
        .collect(Collectors.toList());
  }
}
