package com.emmsale.member.api;

import com.emmsale.member.application.InterestTagService;
import com.emmsale.member.application.dto.InterestTagDeleteRequest;
import com.emmsale.member.application.dto.InterestTagResponse;
import com.emmsale.member.application.dto.InterestTagsRequest;
import com.emmsale.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/interest-tags")
public class InterestTagApi {

  private final InterestTagService interestTagService;

  @PostMapping
  public ResponseEntity<List<InterestTagResponse>> addInterestTag(
      final Member member,
      @RequestBody final InterestTagsRequest interestTagAddRequest
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(interestTagService.addInterestTag(member, interestTagAddRequest));
  }

  @PutMapping
  @ResponseStatus(HttpStatus.CREATED)
  public List<InterestTagResponse> updateInterestTags(
      final Member member,
      @RequestBody final InterestTagsRequest request
  ) {
    return interestTagService.updateInterestTags(member, request);
  }

  @DeleteMapping
  public ResponseEntity<List<InterestTagResponse>> deleteInterestTag(
      final Member member,
      @RequestBody final InterestTagDeleteRequest interestTagDeleteRequest
  ) {
    return ResponseEntity.ok(
        interestTagService.deleteInterestTag(member, interestTagDeleteRequest));
  }

  @GetMapping
  public ResponseEntity<List<InterestTagResponse>> findInterestTags(
      @RequestParam("member_id") final Long memberId) {
    return ResponseEntity.ok(interestTagService.findInterestTags(memberId));
  }
}
