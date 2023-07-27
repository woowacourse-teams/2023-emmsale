package com.emmsale.member.api;

import com.emmsale.member.application.MemberActivityService;
import com.emmsale.member.application.MemberUpdateService;
import com.emmsale.member.application.dto.MemberActivityAddRequest;
import com.emmsale.member.application.dto.MemberActivityDeleteRequest;
import com.emmsale.member.application.dto.MemberActivityInitialRequest;
import com.emmsale.member.application.dto.MemberActivityResponses;
import com.emmsale.member.application.dto.OpenProfileUrlRequest;
import com.emmsale.member.domain.Member;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApi {

  private final MemberActivityService memberActivityService;
  private final MemberUpdateService memberUpdateService;

  @PostMapping("/members")
  public ResponseEntity<Void> register(
      final Member member,
      @RequestBody final MemberActivityInitialRequest memberActivityInitialRequest
  ) {
    memberActivityService.registerCareer(member, memberActivityInitialRequest);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/members/activities")
  public ResponseEntity<List<MemberActivityResponses>> addCareer(
      final Member member,
      @RequestBody final MemberActivityAddRequest memberActivityAddRequest
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(memberActivityService.addCareer(member, memberActivityAddRequest));
  }

  @DeleteMapping("/members/activities")
  public ResponseEntity<List<MemberActivityResponses>> deleteCareer(
      final Member member,
      @RequestBody final MemberActivityDeleteRequest memberActivityDeleteRequest
  ) {
    return ResponseEntity.ok(
        memberActivityService.deleteCareer(member, memberActivityDeleteRequest));
  }

  @GetMapping("/members/activities")
  public ResponseEntity<List<MemberActivityResponses>> findCareer(final Member member) {
    return ResponseEntity.ok(memberActivityService.findCareers(member));
  }

  @PutMapping("/members/open-profile-url")
  public ResponseEntity<Void> updateOpenProfileUrl(
      final Member member,
      @RequestBody @Valid final OpenProfileUrlRequest openProfileUrlRequest
  ) {
    memberUpdateService.updateOpenProfileUrl(member, openProfileUrlRequest);
    return ResponseEntity.noContent().build();
  }
}
