package com.emmsale.event.api;

import static java.lang.String.format;
import static java.net.URI.create;

import com.emmsale.event.application.RecruitmentPostCommandService;
import com.emmsale.event.application.RecruitmentPostQueryService;
import com.emmsale.event.application.dto.RecruitmentPostQueryResponse;
import com.emmsale.event.application.dto.RecruitmentPostRequest;
import com.emmsale.event.application.dto.RecruitmentPostUpdateRequest;
import com.emmsale.member.domain.Member;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class RecruitmentPostApi {

  private final RecruitmentPostCommandService postCommandService;
  private final RecruitmentPostQueryService postQueryService;

  @PostMapping("/{eventId}/recruitment-posts")
  public ResponseEntity<Void> createRecruitmentPost(
      @PathVariable final Long eventId,
      @RequestBody @Valid final RecruitmentPostRequest request,
      final Member member
  ) {
    final Long recruitmentPostId = postCommandService.createRecruitmentPost(eventId, request,
        member);

    return ResponseEntity
        .created(create(format("/events/%s/recruitment-posts/%s", eventId, recruitmentPostId)))
        .build();
  }

  @PutMapping("/{eventId}/recruitment-posts/{recruitment-post-Id}")
  public ResponseEntity<Void> updateRecruitmentPost(
      @PathVariable final Long eventId,
      @PathVariable(name = "recruitment-post-Id") final Long postId,
      @RequestBody @Valid final RecruitmentPostUpdateRequest request,
      final Member member
  ) {
    postCommandService.updateRecruitmentPost(eventId, postId, request, member);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{eventId}/recruitment-posts/{recruitment-post-id}")
  public ResponseEntity<String> deleteRecruitmentPost(
      @PathVariable final Long eventId,
      @PathVariable(name = "recruitment-post-id") final Long postId,
      final Member member
  ) {
    postCommandService.deleteRecruitmentPost(eventId, postId, member);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/recruitment-posts")
  public ResponseEntity<List<RecruitmentPostQueryResponse>> findRecruitmentPosts(
      @PathVariable final Long id) {
    final List<RecruitmentPostQueryResponse> responses = postQueryService.findRecruitmentPosts(id);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/{event-id}/recruitment-posts/{recruitment-post-id}")
  public ResponseEntity<RecruitmentPostQueryResponse> findRecruitmentPost(
      @PathVariable("event-id") final Long eventId,
      @PathVariable("recruitment-post-id") final Long postId
  ) {
    final RecruitmentPostQueryResponse response
        = postQueryService.findRecruitmentPost(eventId, postId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{eventId}/recruitment-posts/already-recruitment")
  public ResponseEntity<Boolean> isAlreadyRecruit(
      @PathVariable final Long eventId,
      @RequestParam(name = "member-id") final Long memberId
  ) {
    return ResponseEntity.ok(postQueryService.isAlreadyRecruit(eventId, memberId));
  }

  @GetMapping("/recruitment-posts")
  public ResponseEntity<List<RecruitmentPostQueryResponse>> findRecruitmentPostsByMemberId(
      final Member member,
      @RequestParam("member-id") final Long memberId
  ) {
    final List<RecruitmentPostQueryResponse> responses
        = postQueryService.findRecruitmentPostsByMemberId(member, memberId);
    return ResponseEntity.ok(responses);
  }
}
