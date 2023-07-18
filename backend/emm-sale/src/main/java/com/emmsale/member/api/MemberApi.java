package com.emmsale.member.api;

import com.emmsale.member.application.MemberCareerService;
import com.emmsale.member.application.dto.MemberCareerAddRequest;
import com.emmsale.member.application.dto.MemberCareerInitialRequest;
import com.emmsale.member.application.dto.MemberCareerResponse;
import com.emmsale.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
