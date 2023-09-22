package com.emmsale.activity.application.dto;

import com.emmsale.activity.domain.Activity;
import lombok.Getter;

@Getter
public class ActivityResponse {

  private final Long id;
  private final String name;

  public ActivityResponse(final Long id, final String name) {
    this.id = id;
    this.name = name;
  }

  public static ActivityResponse from(final Activity activity) {
    return new ActivityResponse(activity.getId(), activity.getName());
  }
}
