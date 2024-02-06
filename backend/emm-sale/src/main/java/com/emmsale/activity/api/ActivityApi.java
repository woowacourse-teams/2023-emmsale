package com.emmsale.activity.api;

import com.emmsale.activity.application.ActivityQueryService;
import com.emmsale.activity.application.dto.ActivityResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityApi {

  private final ActivityQueryService activityQueryService;

  @GetMapping
  public ResponseEntity<List<ActivityResponse>> findAll() {
    return ResponseEntity.ok(activityQueryService.findAll());
  }
}
