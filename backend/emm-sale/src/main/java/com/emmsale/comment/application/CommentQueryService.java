package com.emmsale.comment.application;

import com.emmsale.block.domain.Block;
import com.emmsale.block.domain.BlockRepository;
import com.emmsale.comment.application.dto.CommentHierarchyResponse;
import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.member.domain.Member;
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
  private final BlockRepository blockRepository;

  public List<CommentHierarchyResponse> findAllCommentsByEventId(final Long eventId,
      final Member member) {

    final List<Long> blockedMemberIds = getBlockedMemberIds(member);

    final List<Comment> comments = commentRepository.findByEventId(eventId);

    return CommentHierarchyResponse.convertAllFrom(comments, blockedMemberIds);
  }

  public CommentHierarchyResponse findParentWithChildren(final Long commentId, final Member member) {

    final List<Long> blockedMemberIds = getBlockedMemberIds(member);

    final List<Comment> parentWithChildrenComments
        = commentRepository.findParentAndChildrenByParentId(commentId);

    return CommentHierarchyResponse.from(parentWithChildrenComments, blockedMemberIds);
  }

  private List<Long> getBlockedMemberIds(final Member member) {
    return blockRepository.findAllByRequestMemberId(member.getId())
        .stream()
        .map(Block::getBlockMemberId)
        .collect(Collectors.toList());
  }
}
