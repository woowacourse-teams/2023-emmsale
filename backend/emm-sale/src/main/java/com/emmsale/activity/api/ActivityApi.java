package com.emmsale.activity.api;

import com.emmsale.activity.application.ActivityService;
import com.emmsale.activity.application.dto.ActivityResponses;
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

  private final ActivityService activityService;

  @GetMapping
  public ResponseEntity<List<ActivityResponses>> findAll() {
    return ResponseEntity.ok(activityService.findAll());
  }
}

