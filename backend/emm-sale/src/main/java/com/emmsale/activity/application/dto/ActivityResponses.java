package com.emmsale.activity.application.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class ActivityResponses {

  private final String activityType;
  private final List<ActivityResponse> activityResponses;

  public ActivityResponses(final String activityType, final List<ActivityResponse> activityResponses) {
    this.activityType = activityType;
    this.activityResponses = activityResponses;
  }
}
