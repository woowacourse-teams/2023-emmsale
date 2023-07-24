package com.emmsale.comment.api;

import com.emmsale.comment.application.CommentCommandService;
import com.emmsale.comment.application.dto.CommentAddRequest;
import com.emmsale.comment.application.dto.CommentResponse;
import com.emmsale.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentApi {

  private final CommentCommandService commentCommandService;

  @PostMapping("/comments")
  public CommentResponse create(
      @RequestBody final CommentAddRequest commentAddRequest,
      final Member member
  ) {
    return commentCommandService.create(commentAddRequest, member);
  }
}
