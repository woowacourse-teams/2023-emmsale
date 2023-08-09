package com.emmsale.event.api;

import static java.lang.String.format;
import static java.net.URI.create;

import com.emmsale.event.application.EventService;
import com.emmsale.event.application.dto.RecruitmentPostRequest;
import com.emmsale.event.application.dto.RecruitmentPostResponse;
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

  private final EventService eventService;

  @PostMapping("/{eventId}/recruitment-post")
  public ResponseEntity<Void> createRecruitmentPost(
      @PathVariable final Long eventId,
      @RequestBody @Valid final RecruitmentPostRequest request,
      final Member member
  ) {
    final Long recruitmentPostId = eventService.createRecruitmentPost(eventId, request, member);

    return ResponseEntity
        .created(create(format("/events/%s/recruitment-post/%s", eventId, recruitmentPostId)))
        .build();
  }

  @PutMapping("/{eventId}/recruitment-post/{recruitmentPostId}")
  public ResponseEntity<Void> updateRecruitmentPost(
      @PathVariable final Long eventId,
      @PathVariable final Long recruitmentPostId,
      @RequestBody @Valid final RecruitmentPostUpdateRequest request,
      final Member member
  ) {
    eventService.updateRecruitmentPost(eventId, recruitmentPostId, request, member);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{eventId}/recruitment-post")
  public ResponseEntity<String> deleteRecruitmentPost(
      @PathVariable final Long eventId,
      @RequestParam(name = "member-id") final Long memberId,
      final Member member
  ) {
    eventService.deleteRecruitmentPost(eventId, memberId, member);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/recruitment-post")
  public ResponseEntity<List<RecruitmentPostResponse>> findRecruitmentPosts(
      @PathVariable final Long id) {
    final List<RecruitmentPostResponse> responses = eventService.findRecruitmentPosts(id);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/{eventId}/recruitment-post/already-recruitment")
  public ResponseEntity<Boolean> isAlreadyRecruit(
      @PathVariable final Long eventId,
      @RequestParam(name = "member-id") final Long memberId
  ) {
    return ResponseEntity.ok(eventService.isAlreadyRecruit(eventId, memberId));
  }
}
