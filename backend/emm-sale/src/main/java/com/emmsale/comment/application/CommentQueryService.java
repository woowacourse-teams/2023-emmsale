package com.emmsale.comment.application;

import static com.emmsale.comment.exception.CommentExceptionType.NOT_EVENT_AND_MEMBER_ID_BOTH_NULL;
import static com.emmsale.comment.exception.CommentExceptionType.NOT_FOUND_COMMENT;

import com.emmsale.block.domain.Block;
import com.emmsale.block.domain.BlockRepository;
import com.emmsale.comment.application.dto.CommentFindRequest;
import com.emmsale.comment.application.dto.CommentHierarchyResponse;
import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.comment.exception.CommentException;
import com.emmsale.member.domain.Member;
import java.util.List;
import java.util.Optional;
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

  public List<CommentHierarchyResponse> findAllComments(
      final CommentFindRequest commentFindRequest,
      final Member member
  ) {

    final Optional<Long> optionalMemberId = commentFindRequest.getOptionalMemberId();
    final Long eventId = commentFindRequest.getEventId();

    validateBothNull(optionalMemberId, eventId);

    final List<Long> blockedMemberIds = getBlockedMemberIds(member);

    final List<Comment> comments = optionalMemberId
        .map(commentRepository::findByMemberId)
        .orElse(commentRepository.findByFeedId(eventId));

    return CommentHierarchyResponse.convertAllFrom(comments, blockedMemberIds);
  }

  private void validateBothNull(final Optional<Long> optionalMemberId, final Long eventId) {
    if (optionalMemberId.isEmpty() && eventId == null) {
      throw new CommentException(NOT_EVENT_AND_MEMBER_ID_BOTH_NULL);
    }
  }

  public CommentHierarchyResponse findParentWithChildren(final Long commentId,
      final Member member) {

    final List<Long> blockedMemberIds = getBlockedMemberIds(member);

    final Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentException(NOT_FOUND_COMMENT));

    final List<Comment> parentWithChildrenComments = comment.getParent()
        .map(it -> commentRepository.findParentAndChildrenByParentId(it.getId()))
        .orElseGet(() -> commentRepository.findParentAndChildrenByParentId(commentId));

    return CommentHierarchyResponse.from(parentWithChildrenComments, blockedMemberIds);
  }

  private List<Long> getBlockedMemberIds(final Member member) {
    return blockRepository.findAllByRequestMemberId(member.getId())
        .stream()
        .map(Block::getBlockMemberId)
        .collect(Collectors.toList());
  }
}
