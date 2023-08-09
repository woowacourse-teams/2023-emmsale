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
import com.emmsale.comment.event.UpdateNotificationEvent;
import com.emmsale.comment.exception.CommentException;
import com.emmsale.comment.exception.CommentExceptionType;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.event_publisher.EventPublisher;
import com.emmsale.member.domain.Member;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCommandService {

  private final CommentRepository commentRepository;
  private final EventRepository eventRepository;
  private final EventPublisher eventPublisher;

  public CommentResponse create(
      final CommentAddRequest commentAddRequest,
      final Member member
  ) {
    final Event savedEvent = eventRepository.findById(commentAddRequest.getEventId())
        .orElseThrow(() -> new EventException(EventExceptionType.NOT_FOUND_EVENT));
    final String content = commentAddRequest.getContent();

    final Comment comment = commentAddRequest.optionalParentId()
        .map(commentId -> Comment.createChild(
            savedEvent,
            findSavedComment(commentId),
            member,
            content)
        )
        .orElseGet(() -> Comment.createRoot(savedEvent, member, content));

    final Comment savedComment = commentRepository.save(comment);

    publishNotificationExceptMe(member, savedComment);

    return CommentResponse.from(savedComment);
  }

  private void publishNotificationExceptMe(final Member member, final Comment savedComment) {
    final Set<Comment> notificationCandidates = savedComment.getParent()
        .map(parent -> commentRepository.findParentAndChildrenByParentId(parent.getId())
            .stream()
            .filter(it -> it.isNotMyComment(member.getId()))
            .collect(Collectors.toUnmodifiableSet())
        )
        .orElse(Collections.emptySet());

    notificationCandidates.stream()
        .map(it -> new UpdateNotificationEvent(
            it.getMember().getId(),
            it.getId(),
            it.getClass().getName()))
        .forEach(eventPublisher::publish);
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
