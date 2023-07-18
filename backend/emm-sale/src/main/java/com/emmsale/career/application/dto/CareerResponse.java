package com.emmsale.career.application.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class CareerResponse {

  private final String activityName;
  private final List<ActivityResponse> activityResponses;

  public CareerResponse(final String activityName, final List<ActivityResponse> activityResponses) {
    this.activityName = activityName;
    this.activityResponses = activityResponses;
  }
}
