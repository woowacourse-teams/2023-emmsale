package com.emmsale.comment.api;

import com.emmsale.comment.application.CommentCommandService;
import com.emmsale.comment.application.CommentQueryService;
import com.emmsale.comment.application.dto.CommentAddRequest;
import com.emmsale.comment.application.dto.CommentHierarchyResponse;
import com.emmsale.comment.application.dto.CommentModifyRequest;
import com.emmsale.comment.application.dto.CommentResponse;
import com.emmsale.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentApi {

  private final CommentCommandService commentCommandService;
  private final CommentQueryService commentQueryService;

  @PostMapping("/comments")
  public CommentResponse create(
      @RequestBody final CommentAddRequest commentAddRequest,
      final Member member
  ) {
    return commentCommandService.create(commentAddRequest, member);
  }

  @GetMapping("/comments")
  public List<CommentHierarchyResponse> findAll(@RequestParam final Long eventId) {
    return commentQueryService.findAllCommentsByEventId(eventId);
  }

  @GetMapping("/comments/{comment-id}/children")
  public List<CommentResponse> findChildren(
      @PathVariable("comment-id") final Long commentId
  ) {
    return commentQueryService.findChildrenComments(commentId);
  }

  @DeleteMapping("/comments/{comment-id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("comment-id") final Long commentId, final Member member) {
    commentCommandService.delete(commentId, member);
  }

  @PatchMapping("/comments/{comment-id}")
  public CommentResponse modify(
      @PathVariable("comment-id") final Long commentId,
      final Member member,
      @RequestBody final CommentModifyRequest commentModifyRequest
  ) {
    return commentCommandService.modify(commentId, member, commentModifyRequest);
  }
}
