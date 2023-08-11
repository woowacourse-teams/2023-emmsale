package com.emmsale.member.api;

import com.emmsale.member.application.InterestTagService;
import com.emmsale.member.application.dto.InterestTagRequest;
import com.emmsale.member.application.dto.InterestTagResponse;
import com.emmsale.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class InterestTagApi {

  private final InterestTagService interestTagService;

  @PostMapping("/interest-tags")
  public ResponseEntity<List<InterestTagResponse>> addInterestTag(
      final Member member,
      @RequestBody final InterestTagRequest interestTagRequest
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(interestTagService.addInterestTag(member, interestTagRequest));
  }

  @DeleteMapping("/interest-tags")
  public ResponseEntity<List<InterestTagResponse>> deleteInterestTag(
      final Member member,
      @RequestBody final InterestTagRequest interestTagRequest
  ) {
    return ResponseEntity.ok(
        interestTagService.deleteInterestTag(member, interestTagRequest));
  }

  @GetMapping("/interest-tags")
  public ResponseEntity<List<InterestTagResponse>> findInterestTags(
      @RequestParam("member_id") final Long memberId) {
    return ResponseEntity.ok(interestTagService.findInterestTags(memberId));
  }
}
