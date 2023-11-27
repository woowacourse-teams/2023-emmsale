package com.emmsale.activity.application.dto;

import com.emmsale.activity.domain.Activity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
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
}
