package com.emmsale.activity.application.dto;

import com.emmsale.activity.domain.ActivityType;
import lombok.Getter;

@Getter
public class ActivityAddRequest {

  private final ActivityType activityType;
  private final String name;

  public ActivityAddRequest(final ActivityType activityType, final String name) {
    this.activityType = activityType;
    this.name = name;
  }

}
