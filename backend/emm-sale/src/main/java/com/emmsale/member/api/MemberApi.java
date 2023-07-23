package com.emmsale.member.api;

import com.emmsale.member.application.MemberCareerService;
import com.emmsale.member.application.dto.MemberCareerDeleteRequest;
import com.emmsale.member.application.dto.MemberCareerInitialRequest;
import com.emmsale.member.application.dto.MemberCareerAddRequest;
import com.emmsale.member.application.dto.MemberCareerResponse;
import com.emmsale.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApi {

  private final MemberCareerService memberCareerService;

  @PostMapping("/members")
  public ResponseEntity<Void> register(
      final Member member,
      @RequestBody final MemberCareerInitialRequest memberCareerInitialRequest
  ) {
    memberCareerService.registerCareer(member, memberCareerInitialRequest);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/members/careers")
  public ResponseEntity<List<MemberCareerResponse>> addCareer(
      final Member member,
      @RequestBody final MemberCareerAddRequest memberCareerAddRequest
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(memberCareerService.addCareer(member, memberCareerAddRequest));
  }

  @DeleteMapping("/members/careers")
  public ResponseEntity<List<MemberCareerResponse>> deleteCareer(
      final Member member,
      @RequestBody final MemberCareerDeleteRequest memberCareerDeleteRequest
  ) {
    return ResponseEntity.ok(memberCareerService.deleteCareer(member, memberCareerDeleteRequest));
  }

  @GetMapping("/members/careers")
  public ResponseEntity<List<MemberCareerResponse>> findCareer(final Member member) {
    return ResponseEntity.ok(memberCareerService.findCareers(member));
  }
}
