package com.emmsale.comment.application;

import com.emmsale.comment.application.dto.CommentHierarchyResponse;
import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import java.util.List;
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

    return CommentHierarchyResponse.convertAllFrom(comments);
  }

  public CommentHierarchyResponse findParentWithChildren(final Long commentId) {

    final List<Comment> parentWithChildrenComments
        = commentRepository.findParentAndChildrenByParentId(commentId);

    return CommentHierarchyResponse.from(parentWithChildrenComments);
  }
}
