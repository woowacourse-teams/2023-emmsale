package com.emmsale.comment.application;

import static com.emmsale.comment.exception.CommentExceptionType.FORBIDDEN_DELETE_COMMENT;
import static com.emmsale.comment.exception.CommentExceptionType.FORBIDDEN_MODIFY_COMMENT;
import static com.emmsale.comment.exception.CommentExceptionType.FORBIDDEN_MODIFY_DELETED_COMMENT;
import static com.emmsale.comment.exception.CommentExceptionType.NOT_FOUND_COMMENT;

import com.emmsale.comment.application.dto.CommentAddRequest;
import com.emmsale.comment.application.dto.CommentModifyRequest;
import com.emmsale.comment.application.dto.CommentResponse;
import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.comment.exception.CommentException;
import com.emmsale.comment.exception.CommentExceptionType;
import com.emmsale.event_publisher.EventPublisher;
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
import com.emmsale.feed.exception.FeedException;
import com.emmsale.feed.exception.FeedExceptionType;
import com.emmsale.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCommandService {

  private final CommentRepository commentRepository;
  private final FeedRepository feedRepository;
  private final EventPublisher eventPublisher;

  public CommentResponse create(
      final CommentAddRequest commentAddRequest,
      final Member member
  ) {
    final Feed feed = feedRepository.findById(commentAddRequest.getFeedId())
        .orElseThrow(() -> new FeedException(FeedExceptionType.NOT_FOUND_FEED));
    final String content = commentAddRequest.getContent();

    final Comment comment = commentAddRequest.optionalParentId()
        .map(commentId -> Comment.createChild(
            feed,
            findSavedComment(commentId),
            member,
            content)
        )
        .orElseGet(() -> Comment.createRoot(feed, member, content));

    final Comment savedComment = commentRepository.save(comment);

    publishEvent(savedComment, feed);

    return CommentResponse.from(savedComment);
  }

  private void publishEvent(final Comment comment, final Feed feed) {
    if (comment.getParent().isPresent()) {
      eventPublisher.publish(comment);
      return;
    }
    eventPublisher.publish(comment, feed.getWriter());
  }

  private Comment findSavedComment(final Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentException(NOT_FOUND_COMMENT));
  }

  public void delete(final Long commentId, final Member loginMember) {

    final Comment comment = findSavedComment(commentId);

    validateSameWriter(loginMember, comment, FORBIDDEN_DELETE_COMMENT);

    comment.delete();
  }

  private void validateSameWriter(
      final Member loginMember,
      final Comment comment,
      final CommentExceptionType commentExceptionType
  ) {
    if (loginMember.isNotMe(comment.getMember())) {
      throw new CommentException(commentExceptionType);
    }
  }

  public CommentResponse modify(
      final Long commentId,
      final Member loginMember,
      final CommentModifyRequest commentModifyRequest
  ) {

    final Comment comment = findSavedComment(commentId);

    validateAlreadyDeleted(comment);
    validateSameWriter(loginMember, comment, FORBIDDEN_MODIFY_COMMENT);

    comment.modify(commentModifyRequest.getContent());

    return CommentResponse.from(comment);
  }

  private void validateAlreadyDeleted(final Comment comment) {
    if (comment.isDeleted()) {
      throw new CommentException(FORBIDDEN_MODIFY_DELETED_COMMENT);
    }
  }
}
