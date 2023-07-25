package com.emmsale.comment.api;

import com.emmsale.comment.application.CommentCommandService;
import com.emmsale.comment.application.CommentQueryService;
import com.emmsale.comment.application.dto.CommentAddRequest;
import com.emmsale.comment.application.dto.CommentHierarchyResponse;
import com.emmsale.comment.application.dto.CommentResponse;
import com.emmsale.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
}
