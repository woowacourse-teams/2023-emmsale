package com.emmsale.member.api;

import com.emmsale.member.application.MemberActivityCommandService;
import com.emmsale.member.application.MemberActivityQueryService;
import com.emmsale.member.application.MemberCommandService;
import com.emmsale.member.application.MemberQueryService;
import com.emmsale.member.application.dto.DescriptionRequest;
import com.emmsale.member.application.dto.MemberActivityAddRequest;
import com.emmsale.member.application.dto.MemberActivityInitialRequest;
import com.emmsale.member.application.dto.MemberActivityResponse;
import com.emmsale.member.application.dto.MemberDetailResponse;
import com.emmsale.member.application.dto.MemberImageResponse;
import com.emmsale.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MemberApi {

  private final MemberActivityQueryService memberActivityQueryService;
  private final MemberActivityCommandService memberActivityCommandService;
  private final MemberCommandService memberCommandService;
  private final MemberQueryService memberQueryService;

  @PostMapping("/members")
  public ResponseEntity<Void> register(
      final Member member,
      @RequestBody final MemberActivityInitialRequest memberActivityInitialRequest
  ) {
    memberActivityCommandService.registerActivities(member, memberActivityInitialRequest);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/members/activities")
  public ResponseEntity<List<MemberActivityResponse>> addActivity(
      final Member member,
      @RequestBody final MemberActivityAddRequest memberActivityAddRequest
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(memberActivityCommandService.addActivity(member, memberActivityAddRequest));
  }

  @DeleteMapping("/members/activities")
  public ResponseEntity<List<MemberActivityResponse>> deleteActivity(final Member member,
      @RequestParam("activity-ids") final List<Long> deleteActivityIds) {
    return ResponseEntity.ok(
        memberActivityCommandService.deleteActivity(member, deleteActivityIds));
  }

  @GetMapping("/members/{member-id}/activities")
  public ResponseEntity<List<MemberActivityResponse>> findActivity(
      @PathVariable("member-id") final Long memberId) {
    return ResponseEntity.ok(memberActivityQueryService.findActivities(memberId));
  }

  @PutMapping("/members/description")
  public ResponseEntity<Void> updateDescription(
      final Member member,
      @RequestBody final DescriptionRequest descriptionRequest
  ) {
    memberCommandService.updateDescription(member, descriptionRequest);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/members/{member-id}")
  public ResponseEntity<MemberDetailResponse> findProfile(
      @PathVariable("member-id") final Long memberId) {
    return ResponseEntity.ok(memberQueryService.findProfile(memberId));
  }

  @DeleteMapping("/members/{memberId}")
  public ResponseEntity<Void> deleteMember(
      @PathVariable final Long memberId,
      final Member member
  ) {
    memberCommandService.deleteMember(member, memberId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/members/{memberId}/profile")
  public ResponseEntity<MemberImageResponse> updateProfile(
      @PathVariable final Long memberId,
      @RequestPart final MultipartFile image,
      final Member member
  ) {
    final MemberImageResponse memberImageResponse
        = memberCommandService.updateMemberProfile(image, memberId, member);
    return ResponseEntity.ok(memberImageResponse);
  }
}
