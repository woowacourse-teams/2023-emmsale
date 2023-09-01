package com.emmsale.activity.application;

import com.emmsale.activity.application.dto.ActivityAddRequest;
import com.emmsale.activity.application.dto.ActivityResponse;
import com.emmsale.activity.domain.Activity;
import com.emmsale.activity.domain.ActivityRepository;
import com.emmsale.activity.exception.ActivityException;
import com.emmsale.activity.exception.ActivityExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityCommandService {

  private final ActivityRepository activityRepository;

  public ActivityResponse addActivity(final ActivityAddRequest request) {
    final String name = request.getName();
    final Activity activity = new Activity(request.getActivityType(), name);
    validateAlreadyExist(name);
    return ActivityResponse.from(activityRepository.save(activity));
  }

  private void validateAlreadyExist(final String name) {
    if (activityRepository.existsActivityByName(name)) {
      throw new ActivityException(ActivityExceptionType.ALEADY_EXIST_ACTIVITY);
    }
  }
}

