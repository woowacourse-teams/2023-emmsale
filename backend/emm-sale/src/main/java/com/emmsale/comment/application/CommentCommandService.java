package com.emmsale.comment.application;

import static com.emmsale.comment.exception.CommentExceptionType.CAN_NOT_DELETE_COMMENT;
import static com.emmsale.comment.exception.CommentExceptionType.NOT_FOUND_COMMENT;

import com.emmsale.comment.application.dto.CommentAddRequest;
import com.emmsale.comment.application.dto.CommentResponse;
import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.comment.exception.CommentException;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.member.domain.Member;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCommandService {

  private final CommentRepository commentRepository;
  private final EventRepository eventRepository;

  public CommentResponse create(
      final CommentAddRequest commentAddRequest,
      final Member member
  ) {
    // TODO : Event 조회 Custom Exception 만들면 수정
    final Event savedEvent = eventRepository.findById(commentAddRequest.getEventId())
        .orElseThrow(EntityNotFoundException::new);

    if (isRootComment(commentAddRequest)) {
      return createRootComment(commentAddRequest, member, savedEvent);
    }

    return createChildComment(commentAddRequest, member, savedEvent);
  }

  private boolean isRootComment(final CommentAddRequest commentAddRequest) {
    return commentAddRequest.getParentId() == null;
  }

  private CommentResponse createChildComment(
      final CommentAddRequest commentAddRequest,
      final Member member,
      final Event savedEvent
  ) {
    final Comment savedParentComment = commentRepository.findById(commentAddRequest.getParentId())
        .orElseThrow(() -> new CommentException(NOT_FOUND_COMMENT));

    final Comment savedComment = commentRepository.save(
        new Comment(savedEvent, savedParentComment, member, commentAddRequest.getContent())
    );

    return CommentResponse.from(savedComment);
  }

  private CommentResponse createRootComment(
      final CommentAddRequest commentAddRequest,
      final Member member,
      final Event savedEvent
  ) {
    final Comment savedComment = commentRepository.save(
        new Comment(savedEvent, null, member, commentAddRequest.getContent())
    );

    return CommentResponse.from(savedComment);
  }

  public void delete(final Long commentId, final Member loginMember) {

    final Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CommentException(NOT_FOUND_COMMENT));

    if (loginMember.isNotMe(comment.getMember())) {
      throw new CommentException(CAN_NOT_DELETE_COMMENT);
    }

    comment.delete();
  }
}
