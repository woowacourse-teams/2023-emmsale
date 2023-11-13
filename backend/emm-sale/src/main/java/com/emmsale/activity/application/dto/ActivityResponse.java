package com.emmsale.activity.application.dto;

import com.emmsale.activity.domain.Activity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActivityResponse {

  private final Long id;
  private final String activityType;
  private final String name;

  public static ActivityResponse from(final Activity activity) {
    return new ActivityResponse(
      activity.getId(),
      activity.getActivityType().getValue(),
      activity.getName()
    );
  }

  public Long getId() {
    return id;
  }

  public String getActivityType() {
    return activityType;
  }

  public String getName() {
    return name;
  }
}
