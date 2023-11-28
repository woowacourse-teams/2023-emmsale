package com.emmsale.admin.activity.api;

import com.emmsale.activity.application.dto.ActivityAddRequest;
import com.emmsale.activity.application.dto.ActivityResponse;
import com.emmsale.admin.activity.application.ActivityCommandService;
import com.emmsale.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/activities")
@RequiredArgsConstructor
public class AdminActivityApi {

  private final ActivityCommandService activityCommandService;

  @PostMapping
  public ResponseEntity<ActivityResponse> create(
      @RequestBody final ActivityAddRequest request,
      final Member admin
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(activityCommandService.addActivity(request, admin));
  }
}
